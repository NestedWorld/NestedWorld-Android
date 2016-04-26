package com.nestedworld.nestedworld.api.socket.models.request.combat;

import com.nestedworld.nestedworld.api.socket.models.DefaultModel;

import org.msgpack.value.ValueFactory;

public class FleeRequest implements DefaultModel {

    @Override
    public ValueFactory.MapBuilder serialise() {
        return ValueFactory.newMapBuilder();
    }
}
