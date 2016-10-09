package com.nestedworld.nestedworld.gcm;

import android.content.Context;
import android.support.annotation.NonNull;

import com.nestedworld.nestedworld.database.models.Combat;
import com.nestedworld.nestedworld.gcm.model.NotificationMessage;
import com.nestedworld.nestedworld.helpers.gcm.GcmHelper;
import com.nestedworld.nestedworld.network.socket.implementation.SocketMessageType;
import com.nestedworld.nestedworld.network.socket.models.message.combat.AvailableMessage;
import com.nestedworld.nestedworld.ui.launch.LaunchActivity;

import org.msgpack.value.Value;

import java.util.Map;

public final class NestedWorldGcm {

    /*
    ** Constructor
     */
    private NestedWorldGcm() {
        //Private constructor for avoiding this class to be construct
    }

    /*
    ** Public method
     */
    public static void onMessageReceived(@NonNull final Context context, @NonNull final Map<Value, Value> message, @NonNull final SocketMessageType.MessageKind messageKind, @NonNull final SocketMessageType.MessageKind idKind) {
        NotificationMessage notificationMessage = new NotificationMessage();
        notificationMessage.type = messageKind;
        notificationMessage.content = message;

        handleMessage(context, notificationMessage);
    }

    /*
    ** Internal method
     */
    private static void handleMessage(@NonNull final Context context, @NonNull final NotificationMessage notification) {
        switch (notification.type) {
            case TYPE_COMBAT_AVAILABLE:
                AvailableMessage availableMessage = new AvailableMessage(notification.content);
                Combat combat = availableMessage.saveAsCombat();

                //Display notification
                GcmHelper.displayNotification(context, "Un combat est diposnible : " + combat.origin, LaunchActivity.class);
                break;
            default:
                break;
        }
    }
}
