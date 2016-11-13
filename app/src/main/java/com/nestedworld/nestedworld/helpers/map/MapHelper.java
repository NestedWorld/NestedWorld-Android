package com.nestedworld.nestedworld.helpers.map;

import android.graphics.Point;
import android.support.annotation.NonNull;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;
import com.nestedworld.nestedworld.helpers.log.LogHelper;

import java.util.List;

public final class MapHelper {

    private final static String TAG = MapHelper.class.getSimpleName();

    /*
    ** Constructor
     */
    private MapHelper() {
        //Private constructor for avoiding this class to be construct
    }

    /*
    ** Public method
     */
    public static void displayMarker(@NonNull final GoogleMap googleMap, @NonNull final String markerName, final float markerColor, final double latitude, final double longitude) {
        displayMarker(googleMap, markerName, BitmapDescriptorFactory.defaultMarker(markerColor), latitude, longitude);
    }

    public static Marker displayMarker(@NonNull final GoogleMap googleMap,
                                       @NonNull final String markerName,
                                       @NonNull final BitmapDescriptor markerIcon,
                                       final double latitude,
                                       final double longitude) {
        LogHelper.d(TAG, "displaying marker:" +
                "markerName=" + markerName +
                "latitude=" + latitude +
                "longitude=" + longitude);

        // create marker
        MarkerOptions marker = new MarkerOptions()
                .position(new LatLng(latitude, longitude))
                .title(markerName)
                .icon(markerIcon);

        // adding marker
        return googleMap.addMarker(marker);
    }

    public static void displayArea(@NonNull final GoogleMap googleMap, @NonNull final List<Point> corners, final int color) {
        // Instantiates a new Polyline object and adds points to define a rectangle
        PolygonOptions rectOptions = new PolygonOptions();
        for (Point corner : corners) {
            rectOptions.add(new LatLng(corner.x, corner.y));
        }
        rectOptions.strokeColor(color);

        //add the polygon to the map
        googleMap.addPolygon(rectOptions);
    }


}
