package com.nestedworld.nestedworld.ui.view.mainMenu.tabs.map;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.nestedworld.nestedworld.R;
import com.nestedworld.nestedworld.data.database.entities.Portal;
import com.nestedworld.nestedworld.helpers.map.MapHelper;

import java.util.List;

public class NestedWorldMap {

    private final GoogleMap mGoogleMap;

    /*
     * #############################################################################################
     * # Constructor
     * #############################################################################################
     */
    public NestedWorldMap(@NonNull final GoogleMap googleMap) {
        mGoogleMap = googleMap;
    }

    /*
     * #############################################################################################
     * # Public method
     * #############################################################################################
     */
    public void draw(@NonNull final Context context,
                     @NonNull final List<Portal> portals) {
        //Clear old marker
        mGoogleMap.clear();

        //TODO retrieve icon on aws 
        final Drawable iconDrawable = ContextCompat.getDrawable(context, R.drawable.portal_drawable);

        //Display new marker
        for (Portal portal : portals) {
            final int portalColor = ContextCompat.getColor(context, portal.getColorType());
            final BitmapDescriptor portalIcon = getMarkerIconFromDrawable(iconDrawable, portalColor);

            MapHelper.displayMarker(mGoogleMap,
                    portal.name,
                    portalIcon,
                    portal.latitude,
                    portal.longitude).setTag(portal);
        }
    }

    public void moveCamera(final double latitude,
                           final double longitude,
                           final int zoom) {
        final CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(latitude, longitude)).zoom(zoom).build();
        mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    @NonNull
    public GoogleMap getGoogleMap() {
        return mGoogleMap;
    }

    /*
     * #############################################################################################
     * # Internal method
     * #############################################################################################
     */
    @NonNull
    private BitmapDescriptor getMarkerIconFromDrawable(@NonNull final Drawable drawable,
                                                       @ColorInt final int color) {
        final Canvas canvas = new Canvas();
        final Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        canvas.setBitmap(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.setColorFilter(color, PorterDuff.Mode.OVERLAY);
        drawable.draw(canvas);

        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }
}