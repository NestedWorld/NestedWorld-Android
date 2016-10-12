package com.nestedworld.nestedworld.database.updater;

import android.content.Context;
import android.support.annotation.NonNull;

import com.nestedworld.nestedworld.database.models.Attack;
import com.nestedworld.nestedworld.database.updater.base.EntityUpdater;
import com.nestedworld.nestedworld.events.http.OnAttacksUpdatedEvent;
import com.nestedworld.nestedworld.network.http.models.response.attack.AttacksResponse;

import org.greenrobot.eventbus.EventBus;

import retrofit2.Call;
import retrofit2.Response;

public class AttacksUpdater extends EntityUpdater<AttacksResponse> {

    /*
    ** Constructor
     */
    public AttacksUpdater(@NonNull final Context context) {
        super(context);
    }

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
        //Delete old entity
        Attack.deleteAll(Attack.class);

        //Save entity
        Attack.saveInTx(response.body().attacks);

        //Send event
        EventBus.getDefault().post(new OnAttacksUpdatedEvent());
    }
}
