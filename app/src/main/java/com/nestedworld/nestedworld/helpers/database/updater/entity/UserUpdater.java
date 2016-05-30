package com.nestedworld.nestedworld.helpers.database.updater.entity;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.nestedworld.nestedworld.helpers.database.updater.callback.OnEntityUpdated;
import com.nestedworld.nestedworld.models.User;
import com.nestedworld.nestedworld.network.http.callback.Callback;
import com.nestedworld.nestedworld.network.http.implementation.NestedWorldHttpApi;
import com.nestedworld.nestedworld.network.http.models.response.users.UserResponse;

import retrofit2.Call;
import retrofit2.Response;

public class UserUpdater extends EntityUpdater<UserResponse> {

    public UserUpdater(@NonNull Context context, @NonNull OnEntityUpdated callback) {
        super(context, callback);
    }

    @NonNull
    @Override
    public Call<UserResponse> getRequest() {
        return NestedWorldHttpApi.getInstance(getContext()).getUserInfo();
    }

    @Override
    public void updateEntity(@NonNull Response<UserResponse> response) {
        //Delete old entity
        User.deleteAll(User.class);

        //Save entity
        response.body().user.save();
    }
}
