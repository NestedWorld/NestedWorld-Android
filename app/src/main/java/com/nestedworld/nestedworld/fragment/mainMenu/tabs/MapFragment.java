package com.nestedworld.nestedworld.fragment.mainMenu.tabs;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.nestedworld.nestedworld.R;
import com.nestedworld.nestedworld.api.callback.Callback;
import com.nestedworld.nestedworld.api.errorHandler.RetrofitErrorHandler;
import com.nestedworld.nestedworld.api.implementation.NestedWorldApi;
import com.nestedworld.nestedworld.api.models.apiResponse.places.PlacesResponse;
import com.nestedworld.nestedworld.fragment.base.BaseFragment;
import com.rey.material.widget.ProgressView;

import butterknife.Bind;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * A placeholder fragment containing a simple view.
 */
public class MapFragment extends BaseFragment {

    public final static String FRAGMENT_NAME = MapFragment.class.getSimpleName();

    private final float mUserLat = 36;
    private final float mUserLong = 127;
    private final int mZoom = 10;

    @Bind(R.id.mapView)
    MapView mMapView;

    @Bind(R.id.progressView)
    ProgressView progressView;

    public static void load(@NonNull final FragmentManager fragmentManager) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.fade_out, R.anim.fade_in);
        fragmentTransaction.addToBackStack(FRAGMENT_NAME);
        fragmentTransaction.commit();
    }

    /*
    ** Life cycle
     */
    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_tab_map;
    }

    @Override
    protected void initUI(Bundle savedInstanceState) {
        mMapView.onCreate(savedInstanceState);
        mMapView.onResume();//We display the map immediately

        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                googleMap.setMyLocationEnabled(true);
                googleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);

                moveCamera(mUserLat, mUserLong, mZoom);

                //displaying a loading animation
                progressView.start();

                //making the request for places
                NestedWorldApi.getInstance(mContext).getRegions(new Callback<PlacesResponse>() {
                    @Override
                    public void onSuccess(Response<PlacesResponse> response, Retrofit retrofit) {
                        //request success, we display nearest places
                        for (PlacesResponse.Place place : response.body().places) {
                            if (isPlaceDisplayable(place)) {
                                displayMarker(place.name, BitmapDescriptorFactory.HUE_BLUE, place.latitude(), place.longitude());
                            }
                        }

                        //now we can stop the loading animation
                        progressView.stop();
                    }

                    @Override
                    public void onError(@NonNull KIND errorKind, @Nullable Response<PlacesResponse> response) {
                        //request fail, we stop the loading animation and we display an error message
                        progressView.stop();

                        final String errorMessage = RetrofitErrorHandler.getErrorMessage(mContext, errorKind, getString(R.string.error_place), response);
                        Toast.makeText(mContext, errorMessage, Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }

    @Override
    protected void initLogic(Bundle savedInstanceState) {

    }

    @Override
    public void onResume() {
        super.onResume();
        if (mMapView != null) {
            mMapView.onResume();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mMapView != null) {
            mMapView.onPause();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mMapView != null) {
            mMapView.onDestroy();
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        if (mMapView != null) {
            mMapView.onLowMemory();
        }
    }

    /*
    ** Utils
     */
    private void moveCamera(final double latitude, final double longitude, int zoom) {
        GoogleMap googleMap = mMapView.getMap();

        CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(latitude, longitude)).zoom(zoom).build();
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    private void displayMarker(@NonNull final String markerName, final float markerColor, final double latitude, final double longitude) {
        // create marker
        MarkerOptions marker = new MarkerOptions().position(new LatLng(latitude, longitude)).title(markerName);

        // Changing marker icon
        marker.icon(BitmapDescriptorFactory.defaultMarker(markerColor));

        // adding marker
        GoogleMap googleMap = mMapView.getMap();
        googleMap.addMarker(marker);
    }

    private boolean isPlaceDisplayable(@NonNull final PlacesResponse.Place place) {
        //we calculate the position between the user and the place
        final float ratioLat = Math.abs(place.latitude() - mUserLat);
        final float ratioLong = Math.abs(place.longitude() - mUserLong);

        return true;

        //1degree ~= 100km
//        return (ratioLat < 1.1) && (ratioLong < 1.1);
    }
}
