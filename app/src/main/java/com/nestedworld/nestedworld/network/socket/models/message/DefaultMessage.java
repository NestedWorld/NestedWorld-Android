package com.nestedworld.nestedworld.network.socket.models.message;

import org.msgpack.value.Value;

import java.util.Map;

public interface DefaultMessage {
    void unSerialise(Map<Value, Value> message);
}
