package com.nestedworld.nestedworld.network.socket.listener;

public interface SocketListener {
    void onSocketConnected();

    void onSocketDisconnected();

    void onMessageSent();

    void onMessageReceived(String message);
}
