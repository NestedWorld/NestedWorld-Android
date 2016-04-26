package com.nestedworld.nestedworld.api.socket.models.request.combat;

import com.nestedworld.nestedworld.api.socket.models.DefaultModel;

import org.msgpack.value.ValueFactory;

public class SendAttackRequest implements DefaultModel {
    public int target;
    public int attack;

    @Override
    public ValueFactory.MapBuilder serialise() {
        ValueFactory.MapBuilder mapBuilder = ValueFactory.newMapBuilder();
        mapBuilder.put(ValueFactory.newString("target"), ValueFactory.newInteger(target));
        mapBuilder.put(ValueFactory.newString("attack"), ValueFactory.newInteger(attack));

        return mapBuilder;
    }
}
