package com.nestedworld.nestedworld.data.database.updater;

import android.support.annotation.NonNull;

import com.nestedworld.nestedworld.data.database.entities.AttackDao;
import com.nestedworld.nestedworld.data.database.updater.base.EntityUpdater;
import com.nestedworld.nestedworld.data.network.http.models.response.attacks.AttacksResponse;
import com.nestedworld.nestedworld.events.http.OnAttacksUpdatedEvent;

import org.greenrobot.eventbus.EventBus;

import retrofit2.Call;
import retrofit2.Response;

public class AttacksUpdater extends EntityUpdater<AttacksResponse> {
    /*
     * #############################################################################################
     * # EntityUpdater<AttacksResponse> implementation
     * #############################################################################################
     */
    @NonNull
    @Override
    public Call<AttacksResponse> getRequest() {
        return getApi().getAttacks();
    }

    @Override
    public void updateEntity(@NonNull final Response<AttacksResponse> response) {
        final AttackDao attackDao = getDatabase().getAttackDao();

        //Delete old entity
        attackDao.deleteAll();

        //Save entity
        attackDao.insertInTx(response.body().attacks);

        //Send event
        EventBus.getDefault().post(new OnAttacksUpdatedEvent());
    }
}
