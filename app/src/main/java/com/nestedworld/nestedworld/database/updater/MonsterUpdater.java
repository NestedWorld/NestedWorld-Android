package com.nestedworld.nestedworld.database.updater;

import android.support.annotation.NonNull;

import com.nestedworld.nestedworld.database.models.Monster;
import com.nestedworld.nestedworld.database.updater.base.EntityUpdater;
import com.nestedworld.nestedworld.events.http.OnMonstersUpdatedEvent;
import com.nestedworld.nestedworld.network.http.models.response.monsters.MonstersResponse;

import org.greenrobot.eventbus.EventBus;

import retrofit2.Call;
import retrofit2.Response;

public class MonsterUpdater extends EntityUpdater<MonstersResponse> {
    /*
    ** Life cycle
     */
    @NonNull
    @Override
    public Call<MonstersResponse> getRequest() {
        return getApi().getMonsters();
    }

    @Override
    public void updateEntity(@NonNull Response<MonstersResponse> response) {
        //Delete old entity
        Monster.deleteAll(Monster.class);

        //Save entity
        Monster.saveInTx(response.body().monsters);

        //Send event
        EventBus.getDefault().post(new OnMonstersUpdatedEvent());
    }
}
