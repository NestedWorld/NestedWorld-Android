package com.nestedworld.nestedworld.data.network.socket.models.message.combat;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.nestedworld.nestedworld.data.network.socket.implementation.SocketMessageType;
import com.nestedworld.nestedworld.data.network.socket.models.message.DefaultMessage;

import org.msgpack.value.Value;
import org.msgpack.value.ValueFactory;

import java.util.Map;

public class AskMessage extends DefaultMessage {

    private String mResult;
    private String mMessage;

    /*
     * #############################################################################################
     * # Constructor
     * #############################################################################################
     */
    public AskMessage(@NonNull final Map<Value, Value> message,
                      @NonNull final SocketMessageType.MessageKind messageKind,
                      @Nullable final SocketMessageType.MessageKind idKind) {
        super(message, messageKind, idKind);
    }

    /*
     * #############################################################################################
     * # Getter and Setter
     * #############################################################################################
     */
    public String getMessage() {
        return mMessage;
    }

    public String getResult() {
        return mResult;
    }

    /*
     * #############################################################################################
     * # Default message implementation
     * #############################################################################################
     */
    @Override
    protected void unSerialise(@NonNull Map<Value, Value> message) {
        if (message.containsKey(ValueFactory.newString("mResult"))) {
            mResult = message.get(ValueFactory.newString("mResult")).asStringValue().asString();
        }
        if (message.containsKey(ValueFactory.newString("message"))) {
            mMessage = message.get(ValueFactory.newString("message")).asStringValue().asString();
        }
    }
}
