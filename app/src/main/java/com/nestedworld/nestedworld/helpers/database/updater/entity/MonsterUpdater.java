package com.nestedworld.nestedworld.helpers.database.updater.entity;

import android.content.Context;
import android.support.annotation.NonNull;

import com.nestedworld.nestedworld.helpers.database.updater.callback.OnEntityUpdated;
import com.nestedworld.nestedworld.models.Monster;
import com.nestedworld.nestedworld.network.http.implementation.NestedWorldHttpApi;
import com.nestedworld.nestedworld.network.http.models.response.monsters.MonstersResponse;

import retrofit2.Call;
import retrofit2.Response;

public class MonsterUpdater extends EntityUpdater<MonstersResponse> {

    public MonsterUpdater(@NonNull Context context, @NonNull OnEntityUpdated callback) {
        super(context, callback);
    }

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
    }
}
