package com.nestedworld.nestedworld.network.socket.listener;

import org.msgpack.value.ImmutableValue;

public interface SocketListener {
    void onSocketConnected();

    void onSocketDisconnected();

    void onMessageSent();

    void onMessageReceived(ImmutableValue message);
}
