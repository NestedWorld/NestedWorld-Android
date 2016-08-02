package com.nestedworld.nestedworld.network.socket.models.message;

import android.support.annotation.NonNull;

import org.msgpack.value.Value;

import java.util.Map;

public abstract class DefaultMessage {

    public DefaultMessage(@NonNull final Map<Value, Value> message) {
        this.unSerialise(message);
    }

    protected abstract void unSerialise(Map<Value, Value> message);
}
