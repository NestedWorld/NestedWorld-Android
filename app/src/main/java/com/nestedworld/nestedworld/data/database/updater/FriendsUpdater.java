package com.nestedworld.nestedworld.data.database.updater;

import android.support.annotation.NonNull;

import com.nestedworld.nestedworld.data.database.models.DaoSession;
import com.nestedworld.nestedworld.data.database.models.friend.Friend;
import com.nestedworld.nestedworld.data.database.models.friend.FriendDao;
import com.nestedworld.nestedworld.data.database.models.friend.FriendDataDao;
import com.nestedworld.nestedworld.data.database.updater.base.EntityUpdater;
import com.nestedworld.nestedworld.data.network.http.models.response.users.friend.FriendsResponse;
import com.nestedworld.nestedworld.events.http.OnFriendsUpdatedEvent;

import org.greenrobot.eventbus.EventBus;

import retrofit2.Call;
import retrofit2.Response;

public class FriendsUpdater extends EntityUpdater<FriendsResponse> {

    /*
     * #############################################################################################
     * # EntityUpdater<FriendsResponse> implementation
     * #############################################################################################
     */
    @NonNull
    @Override
    public Call<FriendsResponse> getRequest() {
        return getApi().getFriends();
    }

    @Override
    public void updateEntity(@NonNull final Response<FriendsResponse> response) {
        final DaoSession dataBase = getDatabase();
        final FriendDao friendDao = dataBase.getFriendDao();
        final FriendDataDao friendDataDao = dataBase.getFriendDataDao();

        //Delete old player
        friendDataDao.deleteAll();
        friendDao.deleteAll();

        //Populate foreign key
        for (Friend friend : response.body().friends) {
            friendDataDao.insert(friend.friendData);
            friend.friendDataIdFk = friend.friendData.playerId;
        }

        //Save new entity
        friendDao.insertInTx(response.body().friends);

        //Send event
        EventBus.getDefault().post(new OnFriendsUpdatedEvent());
    }
}
