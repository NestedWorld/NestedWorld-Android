package com.nestedworld.nestedworld.database.updater;

import android.support.annotation.NonNull;

import com.nestedworld.nestedworld.database.models.AttackDao;
import com.nestedworld.nestedworld.database.updater.base.EntityUpdater;
import com.nestedworld.nestedworld.events.http.OnAttacksUpdatedEvent;
import com.nestedworld.nestedworld.network.http.models.response.attack.AttacksResponse;

import org.greenrobot.eventbus.EventBus;

import retrofit2.Call;
import retrofit2.Response;

public class AttacksUpdater extends EntityUpdater<AttacksResponse> {
    /*
    ** Life cycle
     */
    @NonNull
    @Override
    public Call<AttacksResponse> getRequest() {
        return getApi().getAttacks();
    }

    @Override
    public void updateEntity(@NonNull final Response<AttacksResponse> response) {
        AttackDao attackDao = getDatabase().getAttackDao();

        //Delete old entity
        attackDao.deleteAll();

        //Save entity
        attackDao.insertInTx(response.body().attacks);

        //Send event
        EventBus.getDefault().post(new OnAttacksUpdatedEvent());
    }
}
