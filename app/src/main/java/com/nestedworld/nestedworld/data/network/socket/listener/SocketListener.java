package com.nestedworld.nestedworld.data.network.socket.listener;

import android.support.annotation.NonNull;

import org.msgpack.value.ImmutableValue;

public interface SocketListener {
    void onSocketConnected();

    void onSocketDisconnected();

    void onSocketListening();

    void onMessageReceived(@NonNull final ImmutableValue message);
}
