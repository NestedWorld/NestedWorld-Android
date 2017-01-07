package com.nestedworld.nestedworld.helpers.service;

import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.support.annotation.NonNull;

import com.nestedworld.nestedworld.data.network.socket.service.SocketService;
import com.nestedworld.nestedworld.helpers.log.LogHelper;

public final class ServiceHelper {
    private final static String TAG = ServiceHelper.class.getSimpleName();

    /*
     * #############################################################################################
     * # Constructor
     * #############################################################################################
     */
    private ServiceHelper() {
        //Empty constructor for avoiding this class to be construct
    }

    /*
     * #############################################################################################
     * # Public (static) method
     * #############################################################################################
     */
    public static void startSocketService(@NonNull final Context context) {
        LogHelper.d(TAG, "startSocketService");

        //Start the service
        final Intent intent = new Intent(context, SocketService.class);
        context.startService(intent);
    }

    public static void stopSocketService(@NonNull final Context context) {
        LogHelper.d(TAG, "stopSocketService");

        //Start the service
        final Intent intent = new Intent(context, SocketService.class);
        context.stopService(intent);
    }

    public static void bindToSocketService(@NonNull final Context context,
                                           @NonNull final ServiceConnection serviceConnection) {
        LogHelper.d(TAG, "bindToSocketService");

        context.bindService(new Intent(context, SocketService.class), serviceConnection, Context.BIND_AUTO_CREATE);
    }

    public static void unbindFromSocketService(@NonNull final Context context,
                                               @NonNull final ServiceConnection serviceConnection) {
        LogHelper.d(TAG, "bindToSocketService");

        context.unbindService(serviceConnection);
    }
}
