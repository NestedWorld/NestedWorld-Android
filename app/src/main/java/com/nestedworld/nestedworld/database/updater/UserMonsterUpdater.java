package com.nestedworld.nestedworld.database.updater;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.nestedworld.nestedworld.database.models.UserMonster;
import com.nestedworld.nestedworld.database.updater.base.EntityUpdater;
import com.nestedworld.nestedworld.database.updater.callback.OnEntityUpdated;
import com.nestedworld.nestedworld.events.http.OnUserMonstersUpdatedEvent;
import com.nestedworld.nestedworld.network.http.implementation.NestedWorldHttpApi;
import com.nestedworld.nestedworld.network.http.models.response.users.monster.UserMonsterResponse;

import org.greenrobot.eventbus.EventBus;

import retrofit2.Call;
import retrofit2.Response;

public class UserMonsterUpdater extends EntityUpdater<UserMonsterResponse> {

    /*
    ** Constructor
     */
    public UserMonsterUpdater(@NonNull final Context context, @Nullable final OnEntityUpdated callback) {
        super(context, callback);
    }

    /*
    ** Life cycle
     */
    @NonNull
    @Override
    public Call<UserMonsterResponse> getRequest() {
        return NestedWorldHttpApi.getInstance(getContext()).getUserMonster();
    }

    @Override
    public void updateEntity(@NonNull final Response<UserMonsterResponse> response) {
        //Delete old entity
        UserMonster.deleteAll(UserMonster.class);

        //Update foreign key
        for (UserMonster userMonster : response.body().monsters) {
            userMonster.fkmonster = userMonster.infos.monsterId;
        }

        //Save entity
        UserMonster.saveInTx(response.body().monsters);

        //Send event
        EventBus.getDefault().post(new OnUserMonstersUpdatedEvent());
    }
}
