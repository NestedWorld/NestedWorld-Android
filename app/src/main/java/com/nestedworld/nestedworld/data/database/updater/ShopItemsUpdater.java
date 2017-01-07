package com.nestedworld.nestedworld.data.database.updater;

import android.support.annotation.NonNull;

import com.nestedworld.nestedworld.data.database.entities.ShopItemDao;
import com.nestedworld.nestedworld.data.database.updater.base.EntityUpdater;
import com.nestedworld.nestedworld.data.network.http.models.response.objects.ObjectsResponse;
import com.nestedworld.nestedworld.events.http.OnShopItemsUpdated;

import org.greenrobot.eventbus.EventBus;

import retrofit2.Call;
import retrofit2.Response;

public class ShopItemsUpdater extends EntityUpdater<ObjectsResponse> {

    /*
     * #############################################################################################
     * # EntityUpdater<ObjectsResponse> implementation
     * #############################################################################################
     */
    @NonNull
    @Override
    protected Call<ObjectsResponse> getRequest() {
        return getApi().getShopItems();
    }

    @Override
    protected void updateEntity(@NonNull final Response<ObjectsResponse> response) {
        final ShopItemDao shopItemDao = getDatabase().getShopItemDao();

        //Delete old entity
        shopItemDao.deleteAll();

        //Save entity
        shopItemDao.insertInTx(response.body().objects);

        EventBus.getDefault().post(new OnShopItemsUpdated());
    }
}
