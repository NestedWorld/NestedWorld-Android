package com.nestedworld.nestedworld.helpers.database.updater.entity;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.nestedworld.nestedworld.helpers.log.LogHelper;
import com.nestedworld.nestedworld.models.Friend;
import com.nestedworld.nestedworld.models.User;
import com.nestedworld.nestedworld.network.http.callback.Callback;
import com.nestedworld.nestedworld.network.http.implementation.NestedWorldHttpApi;
import com.nestedworld.nestedworld.network.http.models.response.users.friend.FriendsResponse;

import java.util.Iterator;
import java.util.List;

import retrofit2.Response;

public class FriendsUpdater extends EntityUpdater {

    public FriendsUpdater(@NonNull Context context, @NonNull onEntityUpdated callback) {
        super(context, callback);
    }

    @Override
    public void update() {
        NestedWorldHttpApi.getInstance(getContext()).getFriends(new Callback<FriendsResponse>() {
            @Override
            public void onSuccess(Response<FriendsResponse> response) {

                Friend.deleteAll(Friend.class);

                for (Friend friend : response.body().friends) {
                    friend.fkfuser = friend.info.save();
                    friend.save();
                }

                for (Friend friend : Friend.listAll(Friend.class)) {
                    LogHelper.e("Friend: ", friend.toString());
                }

                for (User user : User.listAll(User.class)) {
                    LogHelper.e("User: ", user.toString());
                }

                onFinish(true);
            }

            @Override
            public void onError(@NonNull KIND errorKind, @Nullable Response<FriendsResponse> response) {
                onFinish(false);
            }
        });
    }
}
