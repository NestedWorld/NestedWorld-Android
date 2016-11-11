package com.nestedworld.nestedworld.database.updater;

import android.support.annotation.NonNull;

import com.nestedworld.nestedworld.database.models.Region;
import com.nestedworld.nestedworld.database.updater.base.EntityUpdater;
import com.nestedworld.nestedworld.network.http.models.response.geo.regions.RegionsResponse;

import retrofit2.Call;
import retrofit2.Response;

public class RegionUpdater extends EntityUpdater<RegionsResponse> {
    /*
    ** Life cycle
     */
    @NonNull
    @Override
    protected Call<RegionsResponse> getRequest() {
        return getApi().getRegions();
    }

    @Override
    protected void updateEntity(@NonNull Response<RegionsResponse> response) {
        //Delete old entity
        Region.deleteAll(Region.class);

        //Save new entity
        Region.saveInTx(response.body().regions);
    }
}
