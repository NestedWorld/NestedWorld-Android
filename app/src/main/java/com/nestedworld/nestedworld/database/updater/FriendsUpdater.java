package com.nestedworld.nestedworld.database.updater;

import android.support.annotation.NonNull;

import com.nestedworld.nestedworld.database.models.Friend;
import com.nestedworld.nestedworld.database.models.User;
import com.nestedworld.nestedworld.database.updater.base.EntityUpdater;
import com.nestedworld.nestedworld.events.http.OnFriendsUpdatedEvent;
import com.nestedworld.nestedworld.network.http.models.response.users.friend.FriendsResponse;
import com.orm.query.Select;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class FriendsUpdater extends EntityUpdater<FriendsResponse> {

    /*
    ** Life cycle
     */
    @NonNull
    @Override
    public Call<FriendsResponse> getRequest() {
        return getApi().getFriends();
    }

    @Override
    public void updateEntity(@NonNull final Response<FriendsResponse> response) {
        //Delete old entity
        List<Friend> oldFriend = Select.from(Friend.class).list();
        for (Friend friend : oldFriend) {
            User friendInfo = friend.info();
            if (friendInfo != null) {
                friendInfo.delete();
            }
            friend.delete();
        }

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
