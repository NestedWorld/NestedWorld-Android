package com.nestedworld.nestedworld.network.socket.listener;

import org.msgpack.value.ImmutableValue;

public interface SocketListener {
    void onSocketConnected();

    void onSocketDisconnected();

    void onSocketListening();

    void onMessageReceived(ImmutableValue message);
}
