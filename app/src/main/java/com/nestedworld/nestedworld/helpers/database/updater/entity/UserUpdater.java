package com.nestedworld.nestedworld.helpers.database.updater.entity;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.nestedworld.nestedworld.models.User;
import com.nestedworld.nestedworld.network.http.callback.Callback;
import com.nestedworld.nestedworld.network.http.implementation.NestedWorldHttpApi;
import com.nestedworld.nestedworld.network.http.models.response.users.UserResponse;

import retrofit2.Response;

public class UserUpdater extends EntityUpdater {

    public UserUpdater(@NonNull Context context, @NonNull onEntityUpdated callback) {
        super(context, callback);
    }

    @Override
    public void update() {
        NestedWorldHttpApi.getInstance(getContext()).getUserInfo(new Callback<UserResponse>() {
            @Override
            public void onSuccess(Response<UserResponse> response) {
                User.deleteAll(User.class);
                response.body().user.save();

                onFinish(true);
            }

            @Override
            public void onError(@NonNull KIND errorKind, @Nullable Response<UserResponse> response) {
                onFinish(false);
            }
        });
    }
}
