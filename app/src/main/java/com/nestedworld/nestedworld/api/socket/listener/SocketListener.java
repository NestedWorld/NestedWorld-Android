package com.nestedworld.nestedworld.api.socket.listener;

public abstract class SocketListener {
    public abstract void onSocketConnected();

    public abstract void onSocketDisconnected();

    public abstract void onMessageSent();

    public abstract void onMessageReceived(String message);
}
