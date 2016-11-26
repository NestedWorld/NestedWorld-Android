package com.nestedworld.nestedworld.database.updater;

import android.support.annotation.NonNull;

import com.nestedworld.nestedworld.database.models.DaoSession;
import com.nestedworld.nestedworld.database.models.Friend;
import com.nestedworld.nestedworld.database.models.FriendDao;
import com.nestedworld.nestedworld.database.models.Player;
import com.nestedworld.nestedworld.database.models.PlayerDao;
import com.nestedworld.nestedworld.database.updater.base.EntityUpdater;
import com.nestedworld.nestedworld.events.http.OnFriendsUpdatedEvent;
import com.nestedworld.nestedworld.network.http.models.response.users.friend.FriendsResponse;

import org.greenrobot.eventbus.EventBus;

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
        DaoSession dataBase = getDatabase();
        FriendDao friendDao = dataBase.getFriendDao();
        PlayerDao playerDao = dataBase.getPlayerDao();

        //Delete player linked to friend
        for (Friend friend : friendDao.loadAll()) {
            Player player = friend.getPlayer();
            if (player != null) {
                player.delete();
            }
        }

        //Delete old player
        friendDao.deleteAll();

        //Populate foreign key
        for (Friend friend : response.body().friends) {
            playerDao.insertOrReplace(friend.player);
            friend.setPlayerId(friend.player.playerId);
        }

        //Save new entity
        friendDao.insertInTx(response.body().friends);

        //Send event
        EventBus.getDefault().post(new OnFriendsUpdatedEvent());
    }
}
