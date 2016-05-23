package com.nestedworld.nestedworld.fragments.mainMenu.tabs;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.location.LocationServices;
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
import com.nestedworld.nestedworld.helpers.map.NestedWorldMap;
import com.nestedworld.nestedworld.network.http.callback.Callback;
import com.nestedworld.nestedworld.network.http.errorHandler.RetrofitErrorHandler;
import com.nestedworld.nestedworld.network.http.implementation.NestedWorldHttpApi;
import com.nestedworld.nestedworld.network.http.models.response.places.PlacesResponse;
import com.nestedworld.nestedworld.network.http.models.response.places.regions.RegionResponse;
import com.nestedworld.nestedworld.network.http.models.response.places.regions.RegionsResponse;
import com.nestedworld.nestedworld.fragments.base.BaseFragment;
import com.nestedworld.nestedworld.helpers.log.LogHelper;
import com.nestedworld.nestedworld.helpers.permission.PermissionUtils;
import com.nestedworld.nestedworld.models.Place;
import com.nestedworld.nestedworld.models.Region;
import com.rey.material.widget.ProgressView;

import java.security.Provider;
import java.util.Arrays;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import retrofit2.Response;

/**
 * A placeholder fragment containing a simple view.
 */
public class MapFragment extends BaseFragment implements LocationListener{

    public final static String FRAGMENT_NAME = MapFragment.class.getSimpleName();

    @Bind(R.id.mapView)
    MapView mMapView;
    @Bind(R.id.progressView)
    ProgressView progressView;
    private NestedWorldMap mMap;

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

                //Check if fragment hasn't been detach
                if (mContext == null) {
                    return;
                }

                //Init the mapView
                mMap = new NestedWorldMap(mContext, googleMap);
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
    ** private method
     */
    private void initMap() {
        //check if fragment hasn't been detach
        if (mContext == null) {
            return;
        }

        /*We check if we are allowed to retrieve the userPosition*/
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            LogHelper.d(TAG, "ACCESS_FINE_LOCATION or ACCESS_COARSE_LOCATION permission needed");

            //We ask for the permission (it we'll call onRequestPermissionsResult who will call initMap())
            PermissionUtils.askForPermissionsFromFragment(mContext, this, Arrays.asList(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION));
            return;
        }

        // Acquire a reference to the system Location Manager
        LocationManager locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);

        //If the provider is already enable
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

            //onProviderEnabled() will not be called so we call it
            onProviderEnabled(LocationManager.GPS_PROVIDER);

            //init location fix
            Location lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (lastLocation != null) {
                onLocationChanged(lastLocation);
            }
        }

        // Register the listener with the Location Manager to receive location updates
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);

        //Center the googleMap map on the userLocation
        mMap.getGoogleMap().setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.getGoogleMap().setMyLocationEnabled(true);
    }

    /*
    ** Location listener
     */
    @Override
    public void onLocationChanged(Location location) {
        LogHelper.d(TAG, "location changed");
        mMap.moveCamera(location.getLatitude(), location.getLongitude(), 12);

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        LogHelper.d(TAG, "status changed");
    }

    @Override
    public void onProviderEnabled(String provider) {
        LogHelper.d(TAG, "provider enable");

        mMap.build(new NestedWorldMap.OnMapReadyListener() {
            @Override
            public void onMapReady() {
                //We stop the loading animation
                progressView.stop();
            }
        });
    }

    @Override
    public void onProviderDisabled(String provider) {
        LogHelper.d(TAG, "provider disable");

        progressView.start();
        //Check if fragment hasn't been detach
        if (mContext == null) {
            return;
        }
        Toast.makeText(mContext, "Can't retrieve your position", Toast.LENGTH_LONG).show();
    }
}
