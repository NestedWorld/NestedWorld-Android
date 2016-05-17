package com.nestedworld.nestedworld.fragments.mainMenu.tabs;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
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
import com.google.android.gms.maps.model.PolygonOptions;
import com.nestedworld.nestedworld.R;
import com.nestedworld.nestedworld.api.http.callback.Callback;
import com.nestedworld.nestedworld.api.http.errorHandler.RetrofitErrorHandler;
import com.nestedworld.nestedworld.api.http.implementation.NestedWorldHttpApi;
import com.nestedworld.nestedworld.api.http.models.response.places.PlacesResponse;
import com.nestedworld.nestedworld.api.http.models.response.places.regions.RegionResponse;
import com.nestedworld.nestedworld.api.http.models.response.places.regions.RegionsResponse;
import com.nestedworld.nestedworld.fragments.base.BaseFragment;
import com.nestedworld.nestedworld.helper.log.LogHelper;
import com.nestedworld.nestedworld.helper.permission.PermissionUtils;
import com.nestedworld.nestedworld.models.Place;
import com.nestedworld.nestedworld.models.Region;
import com.rey.material.widget.ProgressView;

import java.util.Arrays;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import retrofit2.Response;

/**
 * A placeholder fragment containing a simple view.
 */
public class MapFragment extends BaseFragment {

    public final static String FRAGMENT_NAME = MapFragment.class.getSimpleName();

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
    protected int getLayoutResource() {
        return R.layout.fragment_tab_map;
    }

    @Override
    protected void init(View rootView, Bundle savedInstanceState) {
        //we start the loading animation
        if (progressView != null) {
            progressView.start();
        }

        //init MapView
        mMapView.onCreate(savedInstanceState);
        mMapView.onResume();//We display the map immediately


        //retrieve GoogleMap from mapView
        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mGoogleMap = googleMap;

                //Init the mapView
                initMap();
            }
        });
    }

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
        if (mContext == null) {
            return;
        }

        /*We check if we are allowed to retrieve the userPosition*/
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            LogHelper.d(TAG, "ACCESS_FINE_LOCATION or ACCESS_COARSE_LOCATION permission needed");

            //We ask for the permission (it we'll call onRequestPermissionsResult who will init the map)
            PermissionUtils.askForPermissionsFromFragment(mContext, this, Arrays.asList(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION));

        } else {
            //we already have the permission, we can retrieve the userLocation and init the map

            //we retrieve the userLocation
            LocationManager locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
            Criteria criteria = new Criteria();

            Location userLocation = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, true));
            if (userLocation == null) {
                Toast.makeText(mContext, "Can't retrieve your position", Toast.LENGTH_LONG).show();
                return;
            }

            //We center the map on the userLocation
            mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            mGoogleMap.setMyLocationEnabled(true);

            NestedWorldMap nestedWorldMap = new NestedWorldMap(mContext, mGoogleMap);
            nestedWorldMap.moveCamera(userLocation.getLatitude(), userLocation.getLongitude(), 12);
            nestedWorldMap.build(new NestedWorldMap.OnMapReady() {
                @Override
                public void OnMapReady() {
                    //We stop the laoding animation
                    progressView.stop();
                }
            });
        }
    }

    private static class NestedWorldMap {

        private GoogleMap mGoogleMap;
        private final Context mContext;

        public interface OnMapReady {
            void OnMapReady();
        }

        public NestedWorldMap(@NonNull final Context context, @NonNull final GoogleMap googleMap) {
            mGoogleMap = googleMap;
            mContext = context;
        }

        /*
        * Public method
         */
        public void build(@NonNull final OnMapReady onMapInitialize) {
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
        }

        private void moveCamera(final double latitude, final double longitude, int zoom) {
            CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(latitude, longitude)).zoom(zoom).build();
            mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }

        /*
        ** Private method
         */
        private void retrieveAndPoPopulateRegions() {
            NestedWorldHttpApi.getInstance(mContext).getRegions(new Callback<RegionsResponse>() {
                @Override
                public void onSuccess(Response<RegionsResponse> response) {
                    for (Region region : response.body().regions) {
                        retrieveAndPopulateRegion(region);
                    }
                }

                @Override
                public void onError(@NonNull KIND errorKind, @Nullable Response<RegionsResponse> response) {
                    //request fail, we display an error message
                    final String errorMessage = RetrofitErrorHandler.getErrorMessage(mContext, errorKind, mContext.getString(R.string.error_place), response);
                    Toast.makeText(mContext, errorMessage, Toast.LENGTH_LONG).show();
                }
            });
        }

        private void retrieveAndPopulateRegion(@NonNull final Region region) {

            NestedWorldHttpApi.getInstance(mContext).getRegionDetails(region, new Callback<RegionResponse>() {
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
            NestedWorldHttpApi.getInstance(mContext).getPlaces(new Callback<PlacesResponse>() {
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
                    final String errorMessage = RetrofitErrorHandler.getErrorMessage(mContext, errorKind, mContext.getString(R.string.error_place), response);
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
}
