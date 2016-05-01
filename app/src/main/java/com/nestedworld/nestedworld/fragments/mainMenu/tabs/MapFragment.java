package com.nestedworld.nestedworld.fragments.mainMenu.tabs;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.nestedworld.nestedworld.R;
import com.nestedworld.nestedworld.api.http.callback.Callback;
import com.nestedworld.nestedworld.api.http.errorHandler.RetrofitErrorHandler;
import com.nestedworld.nestedworld.api.http.implementation.NestedWorldHttpApi;
import com.nestedworld.nestedworld.api.http.models.response.places.PlacesResponse;
import com.nestedworld.nestedworld.api.http.models.response.places.regions.RegionsResponse;
import com.nestedworld.nestedworld.fragments.base.BaseFragment;
import com.nestedworld.nestedworld.helper.log.LogHelper;
import com.nestedworld.nestedworld.helper.permission.PermissionUtils;
import com.rey.material.widget.ProgressView;

import java.util.Arrays;

import butterknife.Bind;
import retrofit2.Response;

/**
 * A placeholder fragment containing a simple view.
 */
public class MapFragment extends BaseFragment {

    public final static String FRAGMENT_NAME = MapFragment.class.getSimpleName();

    private final static float mUserLat = 37.49377f;
    private final static float mUserLong = 126.88321f;
    private final static int mZoom = 12;

    @Bind(R.id.mapView)
    MapView mMapView;
    @Bind(R.id.progressView)
    ProgressView progressView;
    private GoogleMap mGoogleMap;

    /*
    ** Public method
     */
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        //check for REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS cause we've asked for 2 permission
        if (requestCode != PermissionUtils.REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS) {
            LogHelper.d(TAG, "Wrong request code");
            return;
        }

        //check if the 2 permission were granted
        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                LogHelper.d(TAG, "Permission not granted");
                return;
            }
        }

        initMap();
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_tab_map;
    }

    @Override
    protected void init(View rootView, Bundle savedInstanceState) {

        //we start the loading animation
        if (progressView != null) {
            progressView.start();
        }

        mMapView.onCreate(savedInstanceState);
        mMapView.onResume();//We display the map immediately

        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                //we stop the loading animation
                if (progressView != null) {
                    progressView.stop();
                }

                //init Map
                mGoogleMap = googleMap;
                initMap();
            }
        });
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
    private void initMap() {
        //we check if we have the permission to use the userPosition
        if (mContext == null)
            return;
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            LogHelper.d(TAG, "ACCESS_FINE_LOCATION or ACCESS_COARSE_LOCATION permission needed");

            //We ask for the permission
            PermissionUtils.askForPermissionsFromFragment(mContext, this, Arrays.asList(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION));
            return;
        }

        mGoogleMap.setMyLocationEnabled(true);
        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        moveCamera(mUserLat, mUserLong, mZoom);

        populatePlaces();
        populateRegions();
    }

    private void populatePlaces() {
        if (mContext == null) {
            return;
        }

        //retrieving places from API
        NestedWorldHttpApi.getInstance(mContext).getPlaces(new Callback<PlacesResponse>() {
            @Override
            public void onSuccess(Response<PlacesResponse> response) {
                //request success, we display nearest places
                for (PlacesResponse.Place place : response.body().places) {
                    if (isPlaceDisplayable(place)) {
                        displayMarker(place.name, BitmapDescriptorFactory.HUE_BLUE, place.latitude(), place.longitude());
                    }
                }
            }

            @Override
            public void onError(@NonNull KIND errorKind, @Nullable Response<PlacesResponse> response) {
                //request fail, we display an error message
                final String errorMessage = RetrofitErrorHandler.getErrorMessage(mContext, errorKind, getString(R.string.error_place), response);
                Toast.makeText(mContext, errorMessage, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void populateRegions() {
        if (mContext == null) {
            return;
        }

        NestedWorldHttpApi.getInstance(mContext).getRegions(new Callback<RegionsResponse>() {
            @Override
            public void onSuccess(Response<RegionsResponse> response) {
                //TODO display region on map
            }

            @Override
            public void onError(@NonNull KIND errorKind, @Nullable Response<RegionsResponse> response) {
                //TODO display error message
            }
        });
    }

    private void moveCamera(final double latitude, final double longitude, int zoom) {
        CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(latitude, longitude)).zoom(zoom).build();
        mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    private void displayMarker(@NonNull final String markerName, final float markerColor, final double latitude, final double longitude) {
        // create marker
        MarkerOptions marker = new MarkerOptions().position(new LatLng(latitude, longitude)).title(markerName);

        // Changing marker icon
        marker.icon(BitmapDescriptorFactory.defaultMarker(markerColor));

        // adding marker
        mGoogleMap.addMarker(marker);
    }

    private boolean isPlaceDisplayable(@NonNull final PlacesResponse.Place place) {
        //we calculate the position between the user and the place
        final float ratioLat = Math.abs(place.latitude() - mUserLat);
        final float ratioLong = Math.abs(place.longitude() - mUserLong);

        //TODO remove static distance calculation (should be done on the server)
        //1degree ~= 100km
        return (ratioLat < 1.1) && (ratioLong < 1.1);
    }
}
