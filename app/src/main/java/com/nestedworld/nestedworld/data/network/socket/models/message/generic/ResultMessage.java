package com.nestedworld.nestedworld.data.network.socket.models.message.generic;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.nestedworld.nestedworld.data.network.socket.implementation.SocketMessageType;
import com.nestedworld.nestedworld.data.network.socket.models.message.DefaultMessage;

import org.msgpack.value.Value;

import java.util.Map;

public class ResultMessage extends DefaultMessage {

    private Map<Value, Value> message;

    /*
    ** Constructor
     */
    public ResultMessage(@NonNull Map<Value, Value> message,
                         @NonNull SocketMessageType.MessageKind messageKind,
                         @Nullable SocketMessageType.MessageKind idKind) {
        super(message, messageKind, idKind);
    }

    /*
    ** Default message implementation
     */
    @Override
    protected void unSerialise(@NonNull Map<Value, Value> message) {
        this.message = message;
    }

    /*
    ** Getter & Setter
     */
    public Map<Value, Value> getMessage() {
        return message;
    }
}
