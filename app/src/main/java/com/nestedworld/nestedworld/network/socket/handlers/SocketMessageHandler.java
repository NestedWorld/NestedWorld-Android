package com.nestedworld.nestedworld.network.socket.handlers;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.nestedworld.nestedworld.network.socket.implementation.SocketMessageType;

import org.msgpack.value.Value;

import java.util.Map;

public interface SocketMessageHandler {
    void handleMessage(@NonNull final Map<Value, Value> message,
                       @NonNull final SocketMessageType.MessageKind messageKind,
                       @Nullable final SocketMessageType.MessageKind idKind);
}
