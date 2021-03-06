package com.nestedworld.nestedworld.gcm;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.nestedworld.nestedworld.data.converter.socket.combat.AvailableMessageConverter;
import com.nestedworld.nestedworld.data.database.entities.Combat;
import com.nestedworld.nestedworld.data.database.entities.CombatDao;
import com.nestedworld.nestedworld.data.database.implementation.NestedWorldDatabase;
import com.nestedworld.nestedworld.data.network.socket.implementation.SocketMessageType;
import com.nestedworld.nestedworld.data.network.socket.models.message.combat.AvailableMessage;
import com.nestedworld.nestedworld.gcm.handler.GcmHandler;
import com.nestedworld.nestedworld.gcm.model.NotificationMessage;
import com.nestedworld.nestedworld.helpers.gcm.GcmHelper;
import com.nestedworld.nestedworld.helpers.log.LogHelper;
import com.nestedworld.nestedworld.ui.view.disconnected.launch.LaunchActivity;

import org.msgpack.value.Value;

import java.util.HashMap;
import java.util.Map;

public final class NestedWorldGcm {
    private final static String TAG = NestedWorldGcm.class.getSimpleName();

    private final static Map<SocketMessageType.MessageKind, GcmHandler> mHandlers = new HashMap<SocketMessageType.MessageKind, GcmHandler>() {{
        put(SocketMessageType.MessageKind.TYPE_COMBAT_AVAILABLE, new GcmHandler() {
            @Override
            public void handle(@NonNull Context context,
                               @NonNull NotificationMessage notification,
                               @NonNull SocketMessageType.MessageKind messageKind,
                               @Nullable SocketMessageType.MessageKind idKind) {
                final AvailableMessage availableMessage = new AvailableMessage(notification.content, messageKind, idKind);

                //Convert response into entity
                final Combat combat = new AvailableMessageConverter().convert(availableMessage);

                //Save entity
                NestedWorldDatabase.getInstance()
                        .getDataBase()
                        .getCombatDao()
                        .insertOrReplace(combat);//insertOrReplace cause user can start a combat with himself

                GcmHelper.displayNotification(context, "A new combat is available: " + combat.origin, LaunchActivity.class);
            }
        });
    }};

    /*
     * #############################################################################################
     * # Constructor
     * #############################################################################################
     */
    private NestedWorldGcm() {
        //Private constructor for avoiding this class to be construct
    }

    /*
     * #############################################################################################
     * # Public (static) method
     * #############################################################################################
     */
    public static void onMessageReceived(@NonNull final Context context,
                                         @NonNull final Map<Value, Value> message,
                                         @NonNull final SocketMessageType.MessageKind messageKind,
                                         @Nullable final SocketMessageType.MessageKind idKind) {
        final NotificationMessage notificationMessage = new NotificationMessage();
        notificationMessage.type = messageKind;
        notificationMessage.content = message;

        handleMessage(context, notificationMessage, messageKind, idKind);
    }

    /*
     * #############################################################################################
     * # Internal method
     * #############################################################################################
     */
    private static void handleMessage(@NonNull final Context context,
                                      @NonNull final NotificationMessage notification,
                                      @NonNull final SocketMessageType.MessageKind messageKind,
                                      @Nullable final SocketMessageType.MessageKind idKind) {

        final GcmHandler gcmHandler = mHandlers.get(notification.type);
        if (gcmHandler == null) {
            Log.d(TAG, "Unsuported messageType: " + notification.type);
        } else {
            mHandlers.get(notification.type).handle(context, notification, messageKind, idKind);
        }
    }
}
