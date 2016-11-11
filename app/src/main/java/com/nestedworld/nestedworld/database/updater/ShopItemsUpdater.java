package com.nestedworld.nestedworld.database.updater;

import android.support.annotation.NonNull;
import com.nestedworld.nestedworld.database.models.ShopItem;
import com.nestedworld.nestedworld.database.updater.base.EntityUpdater;
import com.nestedworld.nestedworld.events.http.OnShopItemsUpdated;
import com.nestedworld.nestedworld.network.http.models.response.object.ShopObjectsResponse;

import org.greenrobot.eventbus.EventBus;

import retrofit2.Call;
import retrofit2.Response;

public class ShopItemsUpdater extends EntityUpdater<ShopObjectsResponse> {

    /*
    ** Life cycle
     */
    @NonNull
    @Override
    protected Call<ShopObjectsResponse> getRequest() {
        return getApi().getShopItems();
    }

    @Override
    protected void updateEntity(@NonNull Response<ShopObjectsResponse> response) {
        //Delete old entity
        ShopItem.deleteAll(ShopItem.class);

        //Save new entity
        ShopItem.saveInTx(response.body().objects);

        EventBus.getDefault().post(new OnShopItemsUpdated());
    }
}
