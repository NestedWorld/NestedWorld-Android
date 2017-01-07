package com.nestedworld.nestedworld.data.database.updater;

import android.support.annotation.NonNull;

import com.nestedworld.nestedworld.data.database.entities.Portal;
import com.nestedworld.nestedworld.data.database.entities.PortalDao;
import com.nestedworld.nestedworld.data.database.updater.base.EntityUpdater;
import com.nestedworld.nestedworld.data.network.http.models.response.geo.portal.PortalsResponse;
import com.nestedworld.nestedworld.events.http.OnPortalUpdatedEvent;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class PortalUpdater extends EntityUpdater<PortalsResponse> {

    private final double mLatitude;
    private final double mLongitude;

    /*
     * #############################################################################################
     * # Constructor
     * #############################################################################################
     */
    public PortalUpdater(final double latitude,
                         final double longitude) {
        mLatitude = latitude;
        mLongitude = longitude;
    }

    /*
     * #############################################################################################
     * # EntityUpdater<PortalsResponse> implementation
     * #############################################################################################
     */
    @NonNull
    @Override
    protected Call<PortalsResponse> getRequest() {
        return getApi().getPortals(mLongitude, mLatitude);
    }

    @Override
    protected void updateEntity(@NonNull final Response<PortalsResponse> response) {
        final PortalDao portalDao = getDatabase().getPortalDao();

        //Delete old entity
        portalDao.deleteAll();

        //Parse response
        final List<Portal> newPortals = new ArrayList<>();
        for (PortalsResponse.PortalResponse item : response.body().portals) {
            newPortals.add(item.asPortal());
        }

        //Save entity
        portalDao.insertInTx(newPortals);

        //Notify
        EventBus.getDefault().post(new OnPortalUpdatedEvent());
    }
}
