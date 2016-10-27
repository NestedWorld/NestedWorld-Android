package com.nestedworld.nestedworld.gcm.handler;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.nestedworld.nestedworld.gcm.model.NotificationMessage;
import com.nestedworld.nestedworld.network.socket.implementation.SocketMessageType;

public interface GcmHandler {
    void handle(@NonNull final Context context, @NonNull final NotificationMessage notification, @NonNull final SocketMessageType.MessageKind messageKind, @Nullable final SocketMessageType.MessageKind idKind);
}
