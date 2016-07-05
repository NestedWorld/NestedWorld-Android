package com.nestedworld.nestedworld.network.socket.models.request;

import org.msgpack.value.ValueFactory;

public interface DefaultRequest {
    ValueFactory.MapBuilder serialise();
}