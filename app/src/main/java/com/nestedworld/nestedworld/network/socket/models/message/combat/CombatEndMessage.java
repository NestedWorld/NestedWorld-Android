package com.nestedworld.nestedworld.network.socket.models.message.combat;

import android.support.annotation.NonNull;

import com.nestedworld.nestedworld.network.socket.models.message.DefaultMessage;

import org.msgpack.value.Value;

import java.util.Map;


public class CombatEndMessage extends DefaultMessage {

    /*
    ** Constructor
     */
    public CombatEndMessage(@NonNull Map<Value, Value> message) {
        super(message);
    }

    /*
    ** Life cycle
     */
    @Override
    protected void unSerialise(@NonNull Map<Value, Value> message) {

    }
}
