package com.nestedworld.nestedworld.helpers.service;

import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.support.annotation.NonNull;

import com.nestedworld.nestedworld.service.SocketService;

public final class ServiceHelper {
    /*
    ** Constructor
     */
    private ServiceHelper() {
        //Empty constructor for avoiding this class to be construct
    }

    /*
    ** Life cycle
     */
    public static void startSocketService(@NonNull final Context context) {
        //Start the service
        Intent intent = new Intent(context, SocketService.class);
        context.startService(intent);
    }

    public static void stopSocketService(@NonNull final Context context) {
        //Start the service
        Intent intent = new Intent(context, SocketService.class);
        context.stopService(intent);
    }

    public static void bindToSocketService(@NonNull final Context context, @NonNull final ServiceConnection serviceConnection) {
        context.bindService(new Intent(context, SocketService.class), serviceConnection, Context.BIND_AUTO_CREATE);
    }

    public static void unbindFromSocketService(@NonNull final Context context, @NonNull final ServiceConnection serviceConnection) {
        context.unbindService(serviceConnection);
    }
}
