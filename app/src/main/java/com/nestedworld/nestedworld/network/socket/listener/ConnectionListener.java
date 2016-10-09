package com.nestedworld.nestedworld.network.socket.listener;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.nestedworld.nestedworld.network.socket.implementation.NestedWorldSocketAPI;
import com.nestedworld.nestedworld.network.socket.implementation.SocketMessageType;

import org.msgpack.value.Value;

import java.util.Map;

public interface ConnectionListener {

    /*Will be called when the socket is  available and authenticate*/
    void onConnectionReady(@NonNull final NestedWorldSocketAPI nestedWorldSocketAPI);

    /*Will be called when the socket is closed*/
    void onConnectionLost();

    /*Will be called when something is read on the socket*/
    void onMessageReceived(@NonNull final Map<Value, Value> message, @Nullable final SocketMessageType.MessageKind messageKind, @Nullable final SocketMessageType.MessageKind idKind);
}