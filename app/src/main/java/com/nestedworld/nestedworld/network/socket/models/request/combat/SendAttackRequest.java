package com.nestedworld.nestedworld.network.socket.models.request.combat;

import com.nestedworld.nestedworld.network.socket.models.request.DefaultRequest;

import org.msgpack.value.ValueFactory;

public class SendAttackRequest implements DefaultRequest {
    private int target;
    private int attack;

    public SendAttackRequest(final int target, final int attack) {
        this.target = target;
        this.attack = attack;
    }

    @Override
    public ValueFactory.MapBuilder serialise() {
        ValueFactory.MapBuilder mapBuilder = ValueFactory.newMapBuilder();
        mapBuilder.put(ValueFactory.newString("target"), ValueFactory.newInteger(target));
        mapBuilder.put(ValueFactory.newString("attack"), ValueFactory.newInteger(attack));

        return mapBuilder;
    }
}
