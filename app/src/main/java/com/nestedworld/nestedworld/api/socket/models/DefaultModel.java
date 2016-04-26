package com.nestedworld.nestedworld.api.socket.models;

import org.msgpack.value.ValueFactory;

public interface DefaultModel {
    ValueFactory.MapBuilder serialise();
}
