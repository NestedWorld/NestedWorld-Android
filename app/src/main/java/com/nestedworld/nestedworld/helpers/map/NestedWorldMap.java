package com.nestedworld.nestedworld.helpers.map;

import android.support.annotation.NonNull;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

public class NestedWorldMap {

    private final GoogleMap mGoogleMap;
    /*
    ** Constructor
     */
    public NestedWorldMap(@NonNull final GoogleMap googleMap) {
        mGoogleMap = googleMap;
    }

    /*
    * Public method
     */
    public void build(@NonNull final OnMapUpdatedListener onMapInitialize) {
        //TODO retrieve regions and call MapHelper.displayArea();
        //TODO retrieve portal and call MapHelper.displayMarker();
        onMapInitialize.onMapUpdated();
    }

    public void moveCamera(final double latitude, final double longitude, int zoom) {
        CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(latitude, longitude)).zoom(zoom).build();
        mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    public GoogleMap getGoogleMap() {
        return mGoogleMap;
    }

    /*
    ** Private method
     */

    /*
    ** Inner class
     */
    public interface OnMapUpdatedListener {
        void onMapUpdated();
    }
}