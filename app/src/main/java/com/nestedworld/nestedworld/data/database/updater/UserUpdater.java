package com.nestedworld.nestedworld.data.database.updater;

import android.support.annotation.NonNull;

import com.nestedworld.nestedworld.analytics.NestedWorldAnalytics;
import com.nestedworld.nestedworld.data.database.models.session.SessionDao;
import com.nestedworld.nestedworld.data.database.models.session.SessionData;
import com.nestedworld.nestedworld.data.database.models.session.SessionDataDao;
import com.nestedworld.nestedworld.data.database.updater.base.EntityUpdater;
import com.nestedworld.nestedworld.data.network.http.models.response.users.UserResponse;
import com.nestedworld.nestedworld.events.http.OnUserUpdatedEvent;

import org.greenrobot.eventbus.EventBus;

import retrofit2.Call;
import retrofit2.Response;

public class UserUpdater extends EntityUpdater<UserResponse> {

    /*
     * #############################################################################################
     * # EntityUpdater<UserResponse> implementation
     * #############################################################################################
     */
    @NonNull
    @Override
    public Call<UserResponse> getRequest() {
        return getApi().getUserInfo();
    }

    @Override
    public void updateEntity(@NonNull final Response<UserResponse> response) {
        final SessionDao sessionDao = getDatabase().getSessionDao();
        final SessionDataDao sessionDataDao = getDatabase().getSessionDataDao();

        //Delete old entity
        sessionDao.deleteAll();
        sessionDataDao.deleteAll();

        //Save entity
        final SessionData sessionData = response.body().player;
        sessionDataDao.insert(sessionData);

        //Add sessionData information in our crash report log
        NestedWorldAnalytics.setUserInfo(sessionData);

        //Send event
        EventBus.getDefault().post(new OnUserUpdatedEvent());
    }
}
