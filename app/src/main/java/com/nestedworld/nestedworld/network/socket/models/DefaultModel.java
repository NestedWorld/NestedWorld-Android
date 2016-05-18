package com.nestedworld.nestedworld.network.socket.models;

import org.msgpack.value.ValueFactory;

public interface DefaultModel {
    ValueFactory.MapBuilder serialise();
}
