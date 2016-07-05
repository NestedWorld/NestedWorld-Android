package com.nestedworld.nestedworld.network.socket.listener;

import android.support.annotation.NonNull;

import com.nestedworld.nestedworld.network.socket.implementation.NestedWorldSocketAPI;
import com.nestedworld.nestedworld.network.socket.implementation.SocketMessageType;

import org.msgpack.value.Value;

import java.util.Map;

public interface ConnectionListener {

    void onConnectionReady(@NonNull NestedWorldSocketAPI nestedWorldSocketAPI);

    void onConnectionLost();

    void onMessageReceived(@NonNull SocketMessageType.MessageKind kind, @NonNull Map<Value, Value> content);
}