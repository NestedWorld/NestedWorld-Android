package com.nestedworld.nestedworld.api.socket.models.request.combat;

import com.nestedworld.nestedworld.api.socket.models.DefaultModel;

import org.msgpack.value.ValueFactory;

public class MonsterKoCaptureRequest implements DefaultModel {
    Boolean capture;
    String name;

    @Override
    public ValueFactory.MapBuilder serialise() {
        ValueFactory.MapBuilder mapBuilder = ValueFactory.newMapBuilder();
        mapBuilder.put(ValueFactory.newString("capture"), ValueFactory.newBoolean(capture));
        mapBuilder.put(ValueFactory.newString("name"), ValueFactory.newString(name));

        return mapBuilder;
    }
}
