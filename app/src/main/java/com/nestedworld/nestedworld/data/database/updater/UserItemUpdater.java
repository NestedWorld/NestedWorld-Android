package com.nestedworld.nestedworld.data.database.updater;

import android.support.annotation.NonNull;

import com.nestedworld.nestedworld.data.database.entities.UserItem;
import com.nestedworld.nestedworld.data.database.entities.UserItemDao;
import com.nestedworld.nestedworld.data.database.updater.base.EntityUpdater;
import com.nestedworld.nestedworld.data.network.http.models.response.users.inventory.UserInventoryResponse;
import com.nestedworld.nestedworld.events.http.OnUserItemUpdated;

import org.greenrobot.eventbus.EventBus;

import retrofit2.Call;
import retrofit2.Response;

public class UserItemUpdater extends EntityUpdater<UserInventoryResponse> {

    /*
     * #############################################################################################
     * # EntityUpdater<UserInventoryResponse> implementation
     * #############################################################################################
     */
    @NonNull
    @Override
    protected Call<UserInventoryResponse> getRequest() {
        return getApi().getUserInventory();
    }

    @Override
    protected void updateEntity(@NonNull final Response<UserInventoryResponse> response) {
        final UserItemDao userItemDao = getDatabase().getUserItemDao();

        //Delete old entity
        userItemDao.deleteAll();

        //Save entity
        for (UserItem userItem : response.body().objects) {
            userItem.shopItemId = userItem.shopItem.shopItemId;
        }

        userItemDao.insertInTx(response.body().objects);

        EventBus.getDefault().post(new OnUserItemUpdated());
    }
}
