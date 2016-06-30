package com.nestedworld.nestedworld.network.socket.listener;

import android.support.annotation.NonNull;

import com.nestedworld.nestedworld.network.socket.implementation.NestedWorldSocketAPI;

import org.msgpack.value.Value;

import java.util.Map;

public abstract class ConnectionListener {

    public abstract void onConnectionReady(@NonNull NestedWorldSocketAPI nestedWorldSocketAPI);

    public abstract void onConnectionLost();

    public abstract void onMessageReceived(@NonNull String messageId, @NonNull Map<Value, Value> content);

}