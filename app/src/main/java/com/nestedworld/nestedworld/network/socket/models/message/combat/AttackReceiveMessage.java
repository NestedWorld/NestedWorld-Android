package com.nestedworld.nestedworld.network.socket.models.message.combat;

import android.support.annotation.NonNull;

import com.nestedworld.nestedworld.network.socket.implementation.SocketMessageType;
import com.nestedworld.nestedworld.network.socket.models.message.DefaultMessage;

import org.msgpack.value.Value;

import java.util.Map;

public class AttackReceiveMessage extends DefaultMessage {

    public String type;
    public String id;
    public int attack;
    public int combat;
    public AttackReceiveMessageMonster monster;
    public AttackReceiveMessageTarget target;

    /*
    ** Constructor
     */
    public AttackReceiveMessage(@NonNull Map<Value, Value> message, @NonNull SocketMessageType.MessageKind messageKind, @NonNull SocketMessageType.MessageKind idKind) {
        super(message, messageKind, idKind);
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
        public int id;
        public int hp;
    }

    public class AttackReceiveMessageTarget {
        public int id;
        public int hp;
    }

}
