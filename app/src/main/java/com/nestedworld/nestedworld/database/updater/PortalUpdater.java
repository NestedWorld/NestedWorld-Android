package com.nestedworld.nestedworld.database.updater;

import android.support.annotation.NonNull;

import com.nestedworld.nestedworld.database.models.Portal;
import com.nestedworld.nestedworld.database.updater.base.EntityUpdater;
import com.nestedworld.nestedworld.network.http.models.response.geo.portal.PortalsResponse;

import retrofit2.Call;
import retrofit2.Response;

public class PortalUpdater extends EntityUpdater<PortalsResponse> {
    @NonNull
    @Override
    protected Call<PortalsResponse> getRequest() {
        return getApi().getPortals();
    }

    @Override
    protected void updateEntity(@NonNull Response<PortalsResponse> response) {
        //Delete old entity
        Portal.deleteAll(Portal.class);

        //Save new entity
        Portal.saveInTx(response.body().portals);
    }
}
