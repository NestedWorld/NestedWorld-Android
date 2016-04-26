package com.nestedworld.nestedworld.api.socket.models.request.combat;

import com.nestedworld.nestedworld.api.socket.models.DefaultModel;

import org.msgpack.value.ValueFactory;

public class MonsterKoReplaceRequest implements DefaultModel {
    public int userMonsterId;

    @Override
    public ValueFactory.MapBuilder serialise() {
        ValueFactory.MapBuilder mapBuilder = ValueFactory.newMapBuilder();
        mapBuilder.put(ValueFactory.newString("user_monster_id"), ValueFactory.newInteger(userMonsterId));

        return mapBuilder;
    }
}
