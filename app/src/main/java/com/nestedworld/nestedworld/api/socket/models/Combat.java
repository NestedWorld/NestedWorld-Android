package com.nestedworld.nestedworld.api.socket.models;

import org.msgpack.value.ValueFactory;

public class Combat {

    public static class SendAttack implements DefaultModel {
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
}
