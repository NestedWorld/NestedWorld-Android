package com.nestedworld.nestedworld.network.socket.models.request.combat;

import com.nestedworld.nestedworld.network.socket.models.DefaultModel;

import org.msgpack.value.ValueFactory;

public class MonsterKoCaptureRequest implements DefaultModel {
    public Boolean capture;
    public String name;

    @Override
    public ValueFactory.MapBuilder serialise() {
        ValueFactory.MapBuilder mapBuilder = ValueFactory.newMapBuilder();
        mapBuilder.put(ValueFactory.newString("capture"), ValueFactory.newBoolean(capture));
        mapBuilder.put(ValueFactory.newString("name"), ValueFactory.newString(name));

        return mapBuilder;
    }
}
