package com.nestedworld.nestedworld.helpers.map;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;
import com.nestedworld.nestedworld.R;
import com.nestedworld.nestedworld.models.Place;
import com.nestedworld.nestedworld.models.Region;
import com.nestedworld.nestedworld.network.http.callback.Callback;
import com.nestedworld.nestedworld.network.http.errorHandler.RetrofitErrorHandler;
import com.nestedworld.nestedworld.network.http.implementation.NestedWorldHttpApi;
import com.nestedworld.nestedworld.network.http.models.response.places.PlacesResponse;
import com.nestedworld.nestedworld.network.http.models.response.places.regions.RegionResponse;
import com.nestedworld.nestedworld.network.http.models.response.places.regions.RegionsResponse;

import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import retrofit2.Response;

public class NestedWorldMap {

    private final GoogleMap mGoogleMap;
    private final Context mContext;

    public interface OnMapReadyListener {
        void onMapReady();
    }

    public NestedWorldMap(@NonNull final Context context, @NonNull final GoogleMap googleMap) {
        mGoogleMap = googleMap;
        mContext = context;
    }

    /*
    * Public method
     */
    public void build(@NonNull final OnMapReadyListener onMapInitialize) {
        ThreadPoolExecutor executor = new ThreadPoolExecutor(2, 2, 1, TimeUnit.MINUTES, new SynchronousQueue<Runnable>());

        executor.execute(new Runnable() {
            @Override
            public void run() {
                retrieveAndPopulatePlace();
            }
        });
        executor.execute(new Runnable() {
            @Override
            public void run() {
                retrieveAndPoPopulateRegions();
            }
        });

        // wait for all of the executor threads to finish
        executor.shutdown();
        try {
            if (!executor.awaitTermination(60, TimeUnit.DAYS)) {
                executor.shutdown();
            }
        } catch (InterruptedException e) {
            executor.shutdown();
            Thread.currentThread().interrupt();
            e.printStackTrace();
        }

        onMapInitialize.onMapReady();
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
    private void retrieveAndPoPopulateRegions() {

        NestedWorldHttpApi.getInstance(mContext).getRegions().enqueue(new Callback<RegionsResponse>() {
            @Override
            public void onSuccess(Response<RegionsResponse> response) {
                for (Region region : response.body().regions) {
                    retrieveAndPopulateRegion(region);
                }
            }

            @Override
            public void onError(@NonNull KIND errorKind, @Nullable Response<RegionsResponse> response) {
                //request fail, we display an error message
                final String errorMessage = RetrofitErrorHandler.getErrorMessage(mContext, errorKind, mContext.getString(R.string.error_request_place), response);
                Toast.makeText(mContext, errorMessage, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void retrieveAndPopulateRegion(@NonNull final Region region) {

        NestedWorldHttpApi.getInstance(mContext).getRegionDetails(region).enqueue(new Callback<RegionResponse>() {
            @Override
            public void onSuccess(Response<RegionResponse> response) {
                if (isRegionDisplayable(response.body().region)) {
                    displayRegion(region, Color.BLACK);
                }
            }

            @Override
            public void onError(@NonNull KIND errorKind, @Nullable Response<RegionResponse> response) {
                //TODO display error message
            }
        });
    }


    private void retrieveAndPopulatePlace() {
        //retrieving places from API
        NestedWorldHttpApi.getInstance(mContext).getPlaces().enqueue(new Callback<PlacesResponse>() {
            @Override
            public void onSuccess(Response<PlacesResponse> response) {
                //request success, we display nearest places
                for (Place place : response.body().places) {
                    if (isPlaceDisplayable(place)) {
                        displayMarker(place.name, BitmapDescriptorFactory.HUE_BLUE, place.latitude(), place.longitude());
                    }
                }
            }

            @Override
            public void onError(@NonNull KIND errorKind, @Nullable Response<PlacesResponse> response) {
                //request fail, we display an error message
                final String errorMessage = RetrofitErrorHandler.getErrorMessage(mContext, errorKind, mContext.getString(R.string.error_request_place), response);
                Toast.makeText(mContext, errorMessage, Toast.LENGTH_LONG).show();
            }
        });
    }

    /*
    ** Utils
     */
    private void displayMarker(@NonNull final String markerName, final float markerColor, final double latitude, final double longitude) {
        // create marker
        MarkerOptions marker = new MarkerOptions().position(new LatLng(latitude, longitude)).title(markerName);

        // Changing marker icon
        marker.icon(BitmapDescriptorFactory.defaultMarker(markerColor));

        // adding marker
        mGoogleMap.addMarker(marker);
    }

    private void displayRegion(@NonNull final Region region, final int color) {

        // Instantiates a new Polyline object and adds points to define a rectangle
        PolygonOptions rectOptions = new PolygonOptions();

        //TODO parse and add region corner
            /*for (Point corner : region.corners) {
                rectOptions.add(new LatLng(corner.x, corner.y));
            }*/

        rectOptions.strokeColor(color);

        //add the polygon to the map
        mGoogleMap.addPolygon(rectOptions);
    }

    private boolean isPlaceDisplayable(@NonNull final Place place) {
        //we calculate the position between the user and the place
        final float ratioLat = Math.abs(place.latitude() - (float) mGoogleMap.getCameraPosition().target.latitude);
        final float ratioLong = Math.abs(place.longitude() - (float) mGoogleMap.getCameraPosition().target.longitude);


        //TODO remove static distance calculation (should be done on the server)
        //1degree ~= 100km
        return (ratioLat < 1.1) && (ratioLong < 1.1);
    }

    private boolean isRegionDisplayable(@NonNull final Region region) {
        return false;
    }
}