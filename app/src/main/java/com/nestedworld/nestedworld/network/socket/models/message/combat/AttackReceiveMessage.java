package com.nestedworld.nestedworld.network.socket.models.message.combat;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.nestedworld.nestedworld.network.socket.implementation.SocketMessageType;
import com.nestedworld.nestedworld.network.socket.models.message.DefaultMessage;

import org.msgpack.value.Value;
import org.msgpack.value.ValueFactory;

import java.util.Map;

public class AttackReceiveMessage extends DefaultMessage {

    public AttackReceiveMessageMonster monster;
    public AttackReceiveMessageMonster target;
    private String type;
    private String id;
    private long attack;
    private long combat;

    /*
    ** Constructor
     */
    public AttackReceiveMessage(@NonNull Map<Value, Value> message, @NonNull SocketMessageType.MessageKind messageKind, @Nullable SocketMessageType.MessageKind idKind) {
        super(message, messageKind, idKind);
    }

    /*
    ** Life cycle
     */
    @Override
    protected void unSerialise(@NonNull Map<Value, Value> message) {
        this.type = message.get(ValueFactory.newString("type")).asStringValue().asString();
        this.id = message.get(ValueFactory.newString("id")).asStringValue().asString();
        this.attack = message.get(ValueFactory.newString("attack")).asIntegerValue().asLong();
        this.monster = new AttackReceiveMessageMonster(message.get(ValueFactory.newString("monster")).asMapValue().map(), getMessageKind(), null);
        this.target = new AttackReceiveMessageMonster(message.get(ValueFactory.newString("target")).asMapValue().map(), getMessageKind(), null);
        this.combat = message.get(ValueFactory.newString("combat")).asIntegerValue().asLong();
    }

    @Override
    public String toString() {
        return "AttackReceiveMessage{" +
                "attack=" + attack +
                ", type='" + type + '\'' +
                ", id='" + id + '\'' +
                ", combat=" + combat +
                ", monster=" + monster +
                ", target=" + target +
                '}';
    }

    /*
    /** Inner class used for parsing
     */
    public final static class AttackReceiveMessageMonster extends DefaultMessage {
        public long id;
        public int hp;

        /*
        ** Constructor
         */
        public AttackReceiveMessageMonster(@NonNull Map<Value, Value> message, @NonNull SocketMessageType.MessageKind messageKind, @Nullable SocketMessageType.MessageKind idKind) {
            super(message, messageKind, idKind);
        }

        /*
        ** Life cycle
         */
        @Override
        protected void unSerialise(@NonNull Map<Value, Value> message) {
            this.id = message.get(ValueFactory.newString("id")).asIntegerValue().asLong();
            this.hp = message.get(ValueFactory.newString("hp")).asIntegerValue().asInt();
        }

        /*
        ** Generated
         */
        @Override
        public String toString() {
            return "AttackReceiveMessageMonster{" +
                    "hp=" + hp +
                    ", id=" + id +
                    '}';
        }
    }
}
