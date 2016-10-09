package com.nestedworld.nestedworld.network.socket.models.request.combat;

import com.nestedworld.nestedworld.network.socket.models.request.DefaultRequest;

import org.msgpack.value.ValueFactory;

public class SendAttackRequest implements DefaultRequest {
    private final Integer combatId;
    private final Integer targetId;
    private final Long attackId;

    public SendAttackRequest(final Integer combatId, final Integer targetId, final Long attackId) {
        this.combatId = combatId;
        this.targetId = targetId;
        this.attackId = attackId;
    }

    @Override
    public ValueFactory.MapBuilder serialise() {
        ValueFactory.MapBuilder mapBuilder = ValueFactory.newMapBuilder();
        mapBuilder.put(ValueFactory.newString("combat"), ValueFactory.newInteger(combatId));
        mapBuilder.put(ValueFactory.newString("target"), ValueFactory.newInteger(targetId));
        mapBuilder.put(ValueFactory.newString("attack"), ValueFactory.newInteger(attackId));

        return mapBuilder;
    }
}
