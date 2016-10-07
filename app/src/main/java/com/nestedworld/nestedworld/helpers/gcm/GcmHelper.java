package com.nestedworld.nestedworld.helpers.gcm;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;

import com.nestedworld.nestedworld.R;

public final class GcmHelper {

    /*
    ** Constructor
     */
    private GcmHelper() {
        //Private constructor for avoiding this class to be construct
    }

    /*
    ** Public static method
     */
    public static void displayNotification(@NonNull final Context context, @NonNull final String title, Class<?> target) {
        //Build notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.logo)
                .setContentTitle(context.getString(R.string.app_name))
                .setAutoCancel(true)
                .setColor(ContextCompat.getColor(context, R.color.apptheme_color))
                .setContentText(title);

        //Add action on notification
        Intent intentTarget = new Intent(context, target);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(context, 1, intentTarget, PendingIntent.FLAG_CANCEL_CURRENT);
        builder.setContentIntent(resultPendingIntent);

        //Display notification
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, builder.build());
    }
}
