package com.nestedworld.nestedworld.database.updater;

import android.support.annotation.NonNull;

import com.nestedworld.nestedworld.database.models.UserItem;
import com.nestedworld.nestedworld.database.models.UserItemDao;
import com.nestedworld.nestedworld.database.updater.base.EntityUpdater;
import com.nestedworld.nestedworld.events.http.OnUserItemUpdated;
import com.nestedworld.nestedworld.network.http.models.response.users.inventory.UserInventoryResponse;

import org.greenrobot.eventbus.EventBus;

import retrofit2.Call;
import retrofit2.Response;

public class UserItemUpdater extends EntityUpdater<UserInventoryResponse> {

    @NonNull
    @Override
    protected Call<UserInventoryResponse> getRequest() {
        return getApi().getUserInventory();
    }

    @Override
    protected void updateEntity(@NonNull Response<UserInventoryResponse> response) {
        UserItemDao userItemDao = getDatabase().getUserItemDao();

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
