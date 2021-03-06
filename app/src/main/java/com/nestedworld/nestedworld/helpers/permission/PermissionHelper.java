package com.nestedworld.nestedworld.helpers.permission;

import android.content.Context;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;

import com.nestedworld.nestedworld.helpers.log.LogHelper;
import com.nestedworld.nestedworld.ui.view.base.BaseAppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import static android.support.v4.content.PermissionChecker.checkSelfPermission;

/**
 * Simple for used for 'Android M permission' simplification
 * <p/>
 * Call askForEveryPermissions() if you need multiple permission
 * Call askForPermissionsFromActivity() if you need just one permission
 * <p/>
 * Don't forget to implement :
 * public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
 * in you activity
 */
public final class PermissionHelper {
    public final static int REQUEST_CODE_ASK_PERMISSIONS = 123;
    public final static int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 124;
    private static final String TAG = PermissionHelper.class.getSimpleName();

    /*
     * #############################################################################################
     * # Constructor
     * #############################################################################################
     */
    private PermissionHelper() {
        //Empty constructor for avoiding this class to be construct
    }

    /*
     * #############################################################################################
     * # Public (static) method
     * #############################################################################################
     */
    //asking for multiple permissions from an appcompatActivity
    public static void askForPermissionsFromActivity(@NonNull final Context context,
                                                     @NonNull final List<String> permissionsName) {
        final List<String> permissionNeeded = new ArrayList<>();

        for (String p : permissionsName) {
            if (!isPermissionAllow(context, p)) {
                permissionNeeded.add(p);
                LogHelper.d(TAG, "Asking for permission : " + p);
            }
        }
        ActivityCompat.requestPermissions((BaseAppCompatActivity) context, permissionNeeded.toArray(new String[permissionNeeded.size()]), REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
    }

    //asking for a single permissions from an appcompatActivity
    public static void askForPermissionFromActivity(@NonNull final Context context,
                                                    @NonNull final String permissionName) {
        LogHelper.d(TAG, "Asking for permission : " + permissionName);

        if (checkSelfPermission(context, permissionName) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions((BaseAppCompatActivity) context, new String[]{permissionName}, REQUEST_CODE_ASK_PERMISSIONS);
        }
    }

    //asking for multiple permissions from a v4 fragment
    public static void askForPermissionsFromFragment(@NonNull final Context context,
                                                     @NonNull final Fragment fragment,
                                                     @NonNull final List<String> permissionsName) {
        final List<String> permissionNeeded = new ArrayList<>();

        for (String p : permissionsName) {
            if (!isPermissionAllow(context, p)) {
                permissionNeeded.add(p);
                LogHelper.d(TAG, "Asking for permission : " + p);
            }
        }
        fragment.requestPermissions(permissionNeeded.toArray(new String[permissionNeeded.size()]), REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
    }

    //asking for a single permissions from a v4 fragment
    public static void askForPermissionFromFragment(@NonNull final Context context,
                                                    @NonNull final Fragment fragment,
                                                    @NonNull final String permissionName) {
        LogHelper.d(TAG, "Asking for permission : " + permissionName);

        if (checkSelfPermission(context, permissionName) == PackageManager.PERMISSION_DENIED) {
            fragment.requestPermissions(new String[]{permissionName}, REQUEST_CODE_ASK_PERMISSIONS);
        }
    }

    /*
     * #############################################################################################
     * # Internal method
     * #############################################################################################
     */
    private static boolean isPermissionAllow(@NonNull final Context context,
                                             @NonNull final String permissionName) {
        return checkSelfPermission(context, permissionName) == PackageManager.PERMISSION_GRANTED;
    }
}