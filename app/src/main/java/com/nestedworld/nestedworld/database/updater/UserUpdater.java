package com.nestedworld.nestedworld.database.updater;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.nestedworld.nestedworld.database.models.User;
import com.nestedworld.nestedworld.database.updater.base.EntityUpdater;
import com.nestedworld.nestedworld.database.updater.callback.OnEntityUpdated;
import com.nestedworld.nestedworld.events.http.OnUserUpdatedEvent;
import com.nestedworld.nestedworld.network.http.implementation.NestedWorldHttpApi;
import com.nestedworld.nestedworld.network.http.models.response.users.UserResponse;

import org.greenrobot.eventbus.EventBus;

import retrofit2.Call;
import retrofit2.Response;

public class UserUpdater extends EntityUpdater<UserResponse> {

    /*
    ** Constructor
     */
    public UserUpdater(@NonNull final Context context, @Nullable final OnEntityUpdated callback) {
        super(context, callback);
    }

    /*
    ** Life cycle
     */
    @NonNull
    @Override
    public Call<UserResponse> getRequest() {
        return NestedWorldHttpApi.getInstance(getContext()).getUserInfo();
    }

    @Override
    public void updateEntity(@NonNull final Response<UserResponse> response) {
        //Delete old entity
        User.deleteAll(User.class);

        //Save entity
        response.body().user.save();

        //Send event
        EventBus.getDefault().post(new OnUserUpdatedEvent());
    }
}
