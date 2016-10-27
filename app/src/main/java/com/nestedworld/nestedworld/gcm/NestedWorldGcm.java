package com.nestedworld.nestedworld.gcm;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.nestedworld.nestedworld.database.models.Combat;
import com.nestedworld.nestedworld.gcm.handler.GcmHandler;
import com.nestedworld.nestedworld.gcm.model.NotificationMessage;
import com.nestedworld.nestedworld.helpers.gcm.GcmHelper;
import com.nestedworld.nestedworld.network.socket.implementation.SocketMessageType;
import com.nestedworld.nestedworld.network.socket.models.message.combat.AvailableMessage;
import com.nestedworld.nestedworld.ui.launch.LaunchActivity;

import org.msgpack.value.Value;

import java.util.HashMap;
import java.util.Map;

public final class NestedWorldGcm {
    private final static String TAG = NestedWorldGcm.class.getSimpleName();
    private final static Map<SocketMessageType.MessageKind, GcmHandler> mHandlers = buildHandlers();

    /*
    ** Constructor
     */
    private NestedWorldGcm() {
        //Private constructor for avoiding this class to be construct
    }

    /*
    ** Public method
     */
    public static void onMessageReceived(@NonNull final Context context, @NonNull final Map<Value, Value> message, @NonNull final SocketMessageType.MessageKind messageKind, @Nullable final SocketMessageType.MessageKind idKind) {
        NotificationMessage notificationMessage = new NotificationMessage();
        notificationMessage.type = messageKind;
        notificationMessage.content = message;

        handleMessage(context, notificationMessage, messageKind, idKind);
    }

    /*
    ** Internal method
     */
    private static Map<SocketMessageType.MessageKind, GcmHandler> buildHandlers() {
        Map<SocketMessageType.MessageKind, GcmHandler> handlers = new HashMap<>();
        handlers.put(SocketMessageType.MessageKind.TYPE_COMBAT_AVAILABLE, new GcmHandler() {
            @Override
            public void handle(@NonNull Context context, @NonNull NotificationMessage notification, @NonNull SocketMessageType.MessageKind messageKind, @Nullable SocketMessageType.MessageKind idKind) {
                AvailableMessage availableMessage = new AvailableMessage(notification.content, messageKind, idKind);
                Combat combat = availableMessage.saveAsCombat();

                //Display notification
                GcmHelper.displayNotification(context, "Un combat est diposnible : " + combat.origin, LaunchActivity.class);
            }
        });
        return handlers;
    }

    private static void handleMessage(@NonNull final Context context,
                                      @NonNull final NotificationMessage notification,
                                      @NonNull final SocketMessageType.MessageKind messageKind,
                                      @Nullable final SocketMessageType.MessageKind idKind) {

        GcmHandler gcmHandler = mHandlers.get(notification.type);
        if (gcmHandler == null) {
            Log.d(TAG, "Unsuported messageType" + notification.type);
        } else {
            mHandlers.get(notification.type).handle(context, notification, messageKind, idKind);
        }
    }
}
