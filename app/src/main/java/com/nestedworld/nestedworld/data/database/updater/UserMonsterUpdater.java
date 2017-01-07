package com.nestedworld.nestedworld.data.database.updater;

import android.support.annotation.NonNull;

import com.nestedworld.nestedworld.data.database.models.UserMonster;
import com.nestedworld.nestedworld.data.database.models.UserMonsterDao;
import com.nestedworld.nestedworld.data.database.updater.base.EntityUpdater;
import com.nestedworld.nestedworld.data.network.http.models.response.users.monster.UserMonsterResponse;
import com.nestedworld.nestedworld.events.http.OnUserMonstersUpdatedEvent;

import org.greenrobot.eventbus.EventBus;

import retrofit2.Call;
import retrofit2.Response;

public class UserMonsterUpdater extends EntityUpdater<UserMonsterResponse> {

    /*
     * #############################################################################################
     * # EntityUpdater<UserMonsterResponse> implementation
     * #############################################################################################
     */
    @NonNull
    @Override
    public Call<UserMonsterResponse> getRequest() {
        return getApi().getUserMonster();
    }

    @Override
    public void updateEntity(@NonNull final Response<UserMonsterResponse> response) {
        final UserMonsterDao userMonsterDao = getDatabase().getUserMonsterDao();

        //Delete old entity
        userMonsterDao.deleteAll();

        //Save entity
        for (UserMonster userMonster : response.body().monsters) {
            userMonster.monsterId = userMonster.monster.monsterId;
        }
        userMonsterDao.insertInTx(response.body().monsters);

        //Send event
        EventBus.getDefault().post(new OnUserMonstersUpdatedEvent());
    }
}
