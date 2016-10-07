package com.nestedworld.nestedworld.database.updater;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.nestedworld.nestedworld.events.http.OnMonstersUpdatedEvent;
import com.nestedworld.nestedworld.database.updater.callback.OnEntityUpdated;
import com.nestedworld.nestedworld.database.updater.base.EntityUpdater;
import com.nestedworld.nestedworld.database.models.Monster;
import com.nestedworld.nestedworld.network.http.implementation.NestedWorldHttpApi;
import com.nestedworld.nestedworld.network.http.models.response.monsters.MonstersResponse;

import org.greenrobot.eventbus.EventBus;

import retrofit2.Call;
import retrofit2.Response;

public class MonsterUpdater extends EntityUpdater<MonstersResponse> {

    /*
    ** Constructor
     */
    public MonsterUpdater(@NonNull final Context context, @Nullable final OnEntityUpdated callback) {
        super(context, callback);
    }

    /*
    ** Life cycle
     */
    @NonNull
    @Override
    public Call<MonstersResponse> getRequest() {
        return NestedWorldHttpApi.getInstance(getContext()).getMonsters();
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
