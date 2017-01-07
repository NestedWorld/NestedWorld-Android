package com.nestedworld.nestedworld.data.database.updater;

import android.support.annotation.NonNull;

import com.nestedworld.nestedworld.data.database.entities.MonsterDao;
import com.nestedworld.nestedworld.data.database.updater.base.EntityUpdater;
import com.nestedworld.nestedworld.data.network.http.models.response.monsters.MonstersResponse;
import com.nestedworld.nestedworld.events.http.OnMonstersUpdatedEvent;

import org.greenrobot.eventbus.EventBus;

import retrofit2.Call;
import retrofit2.Response;

public class MonsterUpdater extends EntityUpdater<MonstersResponse> {

    /*
     * #############################################################################################
     * # EntityUpdater<MonstersResponse> implementation
     * #############################################################################################
     */
    @NonNull
    @Override
    public Call<MonstersResponse> getRequest() {
        return getApi().getMonsters();
    }

    @Override
    public void updateEntity(@NonNull final Response<MonstersResponse> response) {
        final MonsterDao monsterDao = getDatabase().getMonsterDao();

        //Delete old entity
        monsterDao.deleteAll();

        //Save entity
        monsterDao.insertInTx(response.body().monsters);

        //Send event
        EventBus.getDefault().post(new OnMonstersUpdatedEvent());
    }
}
