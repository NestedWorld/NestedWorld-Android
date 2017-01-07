package com.nestedworld.nestedworld.data.network.socket.models.message.combat;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.nestedworld.nestedworld.data.network.socket.implementation.SocketMessageType;
import com.nestedworld.nestedworld.data.network.socket.models.message.DefaultMessage;

import org.msgpack.value.Value;
import org.msgpack.value.ValueFactory;

import java.util.Map;

public class AskMessage extends DefaultMessage {

    private String result;
    private String message;

    /*
    ** Constructor
     */
    public AskMessage(@NonNull Map<Value, Value> message,
                      @NonNull SocketMessageType.MessageKind messageKind,
                      @Nullable SocketMessageType.MessageKind idKind) {
        super(message, messageKind, idKind);
    }

    /*
    ** Life cycle
     */
    @Override
    protected void unSerialise(@NonNull Map<Value, Value> message) {
        if (message.containsKey(ValueFactory.newString("result"))) {
            this.result = message.get(ValueFactory.newString("result")).asStringValue().asString();
        }
        if (message.containsKey(ValueFactory.newString("message"))) {
            this.message = message.get(ValueFactory.newString("message")).asStringValue().asString();
        }
    }

    /*
    ** Getter / Setter
     */
    public String getMessage() {
        return message;
    }

    public String getResult() {
        return result;
    }
}
