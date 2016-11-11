package com.nestedworld.nestedworld.database.updater;

import android.support.annotation.NonNull;

import com.nestedworld.nestedworld.database.models.UserItem;
import com.nestedworld.nestedworld.database.updater.base.EntityUpdater;
import com.nestedworld.nestedworld.events.http.OnUserItemUpdated;
import com.nestedworld.nestedworld.network.http.models.response.users.inventory.UserInventoryResponse;

import org.greenrobot.eventbus.EventBus;

import retrofit2.Call;
import retrofit2.Response;

public class UserItemUpdater extends EntityUpdater<UserInventoryResponse>{

    @NonNull
    @Override
    protected Call<UserInventoryResponse> getRequest() {
        return getApi().getUserInventory();
    }

    @Override
    protected void updateEntity(@NonNull Response<UserInventoryResponse> response) {
        //Delete old entity
        UserItem.deleteAll(UserItem.class);

        //Update fk
        for (UserItem userItem : response.body().objects) {
            userItem.shopItemId = userItem.infos.shopItemId;
        }

        //Save entity
        UserItem.saveInTx(response.body().objects);

        EventBus.getDefault().post(new OnUserItemUpdated());
    }
}
