package com.nestedworld.nestedworld.network.socket.models.message.combat;


import android.support.annotation.NonNull;

import com.nestedworld.nestedworld.network.socket.models.message.DefaultMessage;

import org.msgpack.value.Value;
import org.msgpack.value.ValueFactory;

import java.util.Map;

public class MonsterKoMessage extends DefaultMessage {

    public Integer monster;

    public MonsterKoMessage(@NonNull Map<Value, Value> message) {
        super(message);
    }

    @Override
    protected void unSerialise(Map<Value, Value> message) {
        message.get(ValueFactory.newString("monster")).asIntegerValue().asInt();
    }
}
