package com.nestedworld.nestedworld.database.updater;

import android.support.annotation.NonNull;

import com.nestedworld.nestedworld.database.models.Portal;
import com.nestedworld.nestedworld.database.updater.base.EntityUpdater;
import com.nestedworld.nestedworld.events.http.OnPortalUpdatedEvent;
import com.nestedworld.nestedworld.network.http.models.response.geo.portal.PortalsResponse;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class PortalUpdater extends EntityUpdater<PortalsResponse> {

    private final double mLatitude;
    private final double mLongitude;

    /*
    ** Constructor
     */
    public PortalUpdater(final double latitude, final double longitude) {
        mLatitude = latitude;
        mLongitude = longitude;
    }

    /*
    ** Life cycle
     */
    @NonNull
    @Override
    protected Call<PortalsResponse> getRequest() {
        return getApi().getPortals(mLongitude, mLatitude);
    }

    @Override
    protected void updateEntity(@NonNull Response<PortalsResponse> response) {
        //Delete old entity
        com.nestedworld.nestedworld.database.models.Portal.deleteAll(com.nestedworld.nestedworld.database.models.Portal.class);

        List<com.nestedworld.nestedworld.database.models.Portal> newPortals = new ArrayList<>();

        //Save new entity
        for (PortalsResponse.Portal item : response.body().portals) {
            newPortals.add(item.asPortal());
        }
        Portal.saveInTx(newPortals);

        //Notify
        EventBus.getDefault().post(new OnPortalUpdatedEvent());
    }
}
