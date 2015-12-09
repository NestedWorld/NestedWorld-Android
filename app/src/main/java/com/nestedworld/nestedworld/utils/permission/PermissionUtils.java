package com.nestedworld.nestedworld.utils.permission;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import static android.support.v4.app.ActivityCompat.requestPermissions;
import static android.support.v4.content.PermissionChecker.checkSelfPermission;


/**
 * Simple for used for 'Android M permission' simplification
 *
 * Call askForEveryPermissions() if you need multiple permission
 * Call askForPermissions() if you need just one permission
 *
 * Don't forget to implement :
 * public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
 * in you activity
 */
public class PermissionUtils {
    private static String TAG = PermissionUtils.class.getSimpleName();
    final private static int REQUEST_CODE_ASK_PERMISSIONS = 123;
    final private static int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 124;

    /*
    ** Public method
     */
    public static void askForPermissions(@NonNull final Context context, @NonNull final List<String> permissionsName) {
        List<String> permissionNeeded = new ArrayList<>();

        for (String p : permissionsName) {
            if (!isPermissionAllow(context, p)) {
                permissionNeeded.add(p);
            }
        }
        requestPermissions((Activity) context, permissionNeeded.toArray(new String[permissionNeeded.size()]), REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
    }

    public static void askForPermission(@NonNull final Context context, @NonNull final String permissionName) {
        if (checkSelfPermission(context, permissionName) == PackageManager.PERMISSION_DENIED) {
            requestPermissions((Activity) context, new String[]{permissionName}, REQUEST_CODE_ASK_PERMISSIONS);
        }
    }

    /*
    ** Utils
     */
    private static boolean isPermissionAllow(@NonNull final Context context, @NonNull final String permissionName) {
        return checkSelfPermission(context, permissionName) == PackageManager.PERMISSION_GRANTED;
    }
}