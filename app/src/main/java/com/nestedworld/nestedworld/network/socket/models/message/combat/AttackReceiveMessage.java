package com.nestedworld.nestedworld.network.socket.models.message.combat;

import android.support.annotation.NonNull;

import com.nestedworld.nestedworld.network.socket.models.message.DefaultMessage;

import org.msgpack.value.Value;

import java.util.Map;

public class AttackReceiveMessage extends DefaultMessage {

    String type;
    String id;
    int attack;
    int combat;
    AttackReceiveMessageMonster monster;
    AttackReceiveMessagetarget target;
    /*
    ** Constructor
     */
    public AttackReceiveMessage(@NonNull Map<Value, Value> message) {
        super(message);
    }

    /*
    ** Life cycle
     */
    @Override
    protected void unSerialise(@NonNull Map<Value, Value> message) {

    }

    /*
    /** Inner class used for parsing
     */
    public class AttackReceiveMessageMonster {
        int id;
        int hp;
    }

    public class AttackReceiveMessagetarget {
        int id;
        int hp;
    }

}
