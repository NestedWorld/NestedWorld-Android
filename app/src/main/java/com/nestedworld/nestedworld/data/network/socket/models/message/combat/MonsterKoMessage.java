package com.nestedworld.nestedworld.data.network.socket.models.message.combat;


import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.nestedworld.nestedworld.data.network.socket.implementation.SocketMessageType;
import com.nestedworld.nestedworld.data.network.socket.models.message.DefaultMessage;

import org.msgpack.value.Value;
import org.msgpack.value.ValueFactory;

import java.util.Map;

public class MonsterKoMessage extends DefaultMessage {
    public long combat;
    public long monster;


    /*
    ** Constructor
     */
    public MonsterKoMessage(@NonNull Map<Value, Value> message,
                            @NonNull SocketMessageType.MessageKind messageKind,
                            @Nullable SocketMessageType.MessageKind idKind) {
        super(message, messageKind, idKind);
    }

    /*
    ** Life cycle
     */
    @Override
    protected void unSerialise(@NonNull Map<Value, Value> message) {
        this.combat = message.get(ValueFactory.newString("combat")).asIntegerValue().asLong();
        this.monster = message.get(ValueFactory.newString("monster")).asIntegerValue().asLong();
    }
}
