package com.nestedworld.nestedworld.helpers.database.updater.entity;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.nestedworld.nestedworld.events.http.OnFriendsUpdatedEvent;
import com.nestedworld.nestedworld.helpers.database.updater.callback.OnEntityUpdated;
import com.nestedworld.nestedworld.models.Friend;
import com.nestedworld.nestedworld.network.http.implementation.NestedWorldHttpApi;
import com.nestedworld.nestedworld.network.http.models.response.users.friend.FriendsResponse;

import org.greenrobot.eventbus.EventBus;

import retrofit2.Call;
import retrofit2.Response;

public class FriendsUpdater extends EntityUpdater<FriendsResponse> {

    public FriendsUpdater(@NonNull Context context, @Nullable OnEntityUpdated callback) {
        super(context, callback);
    }

    @NonNull
    @Override
    public Call<FriendsResponse> getRequest() {
        return NestedWorldHttpApi.getInstance(getContext()).getFriends();
    }

    @Override
    public void updateEntity(@NonNull Response<FriendsResponse> response) {
        //Delete old entity
        Friend.deleteAll(Friend.class);

        //Update foreign key
        for (Friend friend : response.body().friends) {
            friend.fkfuser = friend.info.save();
        }

        //Save entity
        Friend.saveInTx(response.body().friends);

        //Send event
        EventBus.getDefault().post(new OnFriendsUpdatedEvent());
    }
}
