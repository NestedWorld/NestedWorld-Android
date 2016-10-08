package com.nestedworld.nestedworld.network.socket.models.request.combat;

import com.nestedworld.nestedworld.network.socket.models.request.DefaultRequest;

import org.msgpack.value.ValueFactory;

public class SendAttackRequest implements DefaultRequest {
    private final long target;
    private final long attack;

    public SendAttackRequest(final long target, final long attack) {
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
