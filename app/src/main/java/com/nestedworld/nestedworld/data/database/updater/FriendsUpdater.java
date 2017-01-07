package com.nestedworld.nestedworld.data.database.updater;

import android.support.annotation.NonNull;

import com.nestedworld.nestedworld.data.database.entities.DaoSession;
import com.nestedworld.nestedworld.data.database.entities.friend.Friend;
import com.nestedworld.nestedworld.data.database.entities.friend.FriendDao;
import com.nestedworld.nestedworld.data.database.entities.friend.FriendDataDao;
import com.nestedworld.nestedworld.data.database.updater.base.EntityUpdater;
import com.nestedworld.nestedworld.data.network.http.models.response.users.friend.FriendsResponse;
import com.nestedworld.nestedworld.events.http.OnFriendsUpdatedEvent;
import com.nestedworld.nestedworld.helpers.log.LogHelper;

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

        //Delete old friend
        friendDataDao.deleteAll();
        friendDao.deleteAll();

        //Populate foreign key
        for (Friend friend : response.body().friends) {
            LogHelper.d(TAG, "adding friend: " + friend.toString());

            //Add foreign key and save friend
            friend.friendDataIdFk = friend.friendData.playerId;
            friendDao.save(friend);

            //Add friendData in ORM
            friendDataDao.insert(friend.friendData);
        }

        //Send event
        EventBus.getDefault().post(new OnFriendsUpdatedEvent());
    }
}
