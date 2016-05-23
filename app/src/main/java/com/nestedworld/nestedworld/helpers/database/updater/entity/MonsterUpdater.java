package com.nestedworld.nestedworld.helpers.database.updater.entity;


import android.content.Context;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.nestedworld.nestedworld.models.Monster;
import com.nestedworld.nestedworld.network.http.callback.Callback;
import com.nestedworld.nestedworld.network.http.implementation.NestedWorldHttpApi;
import com.nestedworld.nestedworld.network.http.models.response.monsters.MonstersResponse;

import retrofit2.Response;

public class MonsterUpdater extends EntityUpdater {

    public MonsterUpdater(@NonNull Context context, @NonNull onEntityUpdated callback) {
        super(context, callback);
    }

    @Override
    public void update() {
        NestedWorldHttpApi.getInstance(getContext()).getMonsters(new Callback<MonstersResponse>() {
            @Override
            public void onSuccess(Response<MonstersResponse> response) {

                Monster.deleteAll(Monster.class);

                for (Monster monster : response.body().monsters) {
                    monster.save();
                }

                onFinish(true);
            }

            @Override
            public void onError(@NonNull KIND errorKind, @Nullable Response<MonstersResponse> response) {
                onFinish(false);
            }
        });
    }
}
