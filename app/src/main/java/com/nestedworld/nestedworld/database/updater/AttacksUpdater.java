package com.nestedworld.nestedworld.database.updater;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.nestedworld.nestedworld.database.models.Attack;
import com.nestedworld.nestedworld.database.updater.base.EntityUpdater;
import com.nestedworld.nestedworld.database.updater.callback.OnEntityUpdated;
import com.nestedworld.nestedworld.events.http.OnAttacksUpdatedEvent;
import com.nestedworld.nestedworld.network.http.implementation.NestedWorldHttpApi;
import com.nestedworld.nestedworld.network.http.models.response.attack.AttacksResponse;

import org.greenrobot.eventbus.EventBus;

import retrofit2.Call;
import retrofit2.Response;

public class AttacksUpdater extends EntityUpdater<AttacksResponse> {

    /*
    ** Constructor
     */
    public AttacksUpdater(@NonNull final Context context, @Nullable final OnEntityUpdated callback) {
        super(context, callback);
    }

    /*
    ** Life cycle
     */
    @NonNull
    @Override
    public Call<AttacksResponse> getRequest() {
        return NestedWorldHttpApi.getInstance(getContext()).getAttacks();
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
