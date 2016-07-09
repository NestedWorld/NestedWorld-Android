package com.nestedworld.nestedworld.network.socket.models.message.combat;


import com.nestedworld.nestedworld.network.socket.models.message.DefaultMessage;

import org.msgpack.value.Value;
import org.msgpack.value.ValueFactory;

import java.util.Map;

public class MonsterKoMessage implements DefaultMessage {

    public Integer monster;

    @Override
    public void unSerialise(Map<Value, Value> message) {
        message.get(ValueFactory.newString("monster")).asIntegerValue().asInt();
    }
}
