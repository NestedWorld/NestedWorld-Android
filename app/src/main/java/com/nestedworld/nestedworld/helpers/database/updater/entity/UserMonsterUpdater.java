package com.nestedworld.nestedworld.helpers.database.updater.entity;

import android.content.Context;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.nestedworld.nestedworld.models.UserMonster;
import com.nestedworld.nestedworld.network.http.callback.Callback;
import com.nestedworld.nestedworld.network.http.implementation.NestedWorldHttpApi;
import com.nestedworld.nestedworld.network.http.models.response.users.monster.UserMonsterResponse;

import retrofit2.Response;

public class UserMonsterUpdater extends EntityUpdater {

    public UserMonsterUpdater(@NonNull Context context, @NonNull onEntityUpdated callback) {
        super(context, callback);
    }

    @Override
    public void update() {
        NestedWorldHttpApi.getInstance(getContext()).getUserMonster(new Callback<UserMonsterResponse>() {
            @Override
            public void onSuccess(Response<UserMonsterResponse> response) {

                UserMonster.deleteAll(UserMonster.class);

                for (UserMonster userMonster : response.body().monsters) {
                    userMonster.fkmonster = userMonster.infos.monster_id;
                    userMonster.save();
                }

                onFinish(true);
            }

            @Override
            public void onError(@NonNull KIND errorKind, @Nullable Response<UserMonsterResponse> response) {
                onFinish(false);
            }
        });
    }
}
