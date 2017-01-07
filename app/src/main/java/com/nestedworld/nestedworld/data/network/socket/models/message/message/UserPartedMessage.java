package com.nestedworld.nestedworld.data.network.socket.models.message.message;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.nestedworld.nestedworld.data.network.socket.implementation.SocketMessageType;
import com.nestedworld.nestedworld.data.network.socket.models.message.DefaultMessage;

import org.msgpack.value.Value;

import java.util.Map;

public class UserPartedMessage extends DefaultMessage {

    /*
    ** Constructor
     */
    public UserPartedMessage(@NonNull Map<Value, Value> message,
                             @NonNull SocketMessageType.MessageKind messageKind,
                             @Nullable SocketMessageType.MessageKind idKind) {
        super(message, messageKind, idKind);
    }

    /*
    ** Life cycle
     */
    @Override
    protected void unSerialise(@NonNull Map<Value, Value> message) {

    }
}
