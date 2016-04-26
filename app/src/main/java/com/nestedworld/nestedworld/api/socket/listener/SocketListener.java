package com.nestedworld.nestedworld.api.socket.listener;

import android.support.annotation.NonNull;

public abstract class SocketListener {
    public enum SocketEvent {
        SOCKET_MESSAGE_SENT,
        SOCKET_MESSAGE_RECEIVED,
        SOCKET_DISCONNECTED,
        SOCKET_CONNECTED
    }

    public abstract void onSocketConnected(@NonNull SocketEvent socketEvent);
    public abstract void onSocketDisconnected(@NonNull SocketEvent socketEvent);
    public abstract void onMessageSent(@NonNull SocketEvent socketEven);
    public abstract void onMessageReceived(@NonNull SocketEvent socketEvent, String message);
}
