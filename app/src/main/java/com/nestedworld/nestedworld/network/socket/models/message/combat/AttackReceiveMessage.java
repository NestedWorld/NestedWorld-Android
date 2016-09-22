package com.nestedworld.nestedworld.network.socket.models.message.combat;

import android.support.annotation.NonNull;

import com.nestedworld.nestedworld.network.socket.models.message.DefaultMessage;

import org.msgpack.value.Value;

import java.util.Map;

public class AttackReceiveMessage extends DefaultMessage {


    public AttackReceiveMessage(@NonNull Map<Value, Value> message) {
        super(message);
    }

    @Override
    protected void unSerialise(Map<Value, Value> message) {

    }
}
