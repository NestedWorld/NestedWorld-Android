package com.nestedworld.nestedworld.network.socket.models.request.combat;

import com.nestedworld.nestedworld.network.socket.models.request.DefaultRequest;

import org.msgpack.value.ValueFactory;

public class SendAttackRequest implements DefaultRequest {
    private final long targetId;
    private final long attackId;
    private final long combatId;

    public SendAttackRequest(final long combatId, final long targetId, final long attackId) {
        this.targetId = targetId;
        this.attackId = attackId;
        this.combatId = combatId;
    }

    @Override
    public ValueFactory.MapBuilder serialise() {
        ValueFactory.MapBuilder mapBuilder = ValueFactory.newMapBuilder();
        mapBuilder.put(ValueFactory.newString("target"), ValueFactory.newInteger(targetId));
        mapBuilder.put(ValueFactory.newString("attack"), ValueFactory.newInteger(attackId));
        mapBuilder.put(ValueFactory.newString("combat"), ValueFactory.newInteger(combatId));

        return mapBuilder;
    }
}
