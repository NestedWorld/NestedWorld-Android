package com.nestedworld.nestedworld.network.socket.models.message.combat;

import android.support.annotation.NonNull;

import com.nestedworld.nestedworld.network.socket.models.message.DefaultMessage;

import org.msgpack.value.Value;
import org.msgpack.value.ValueFactory;

import java.util.Map;

public class AskMessage extends DefaultMessage {

    private String type;
    private String message_id;
    private String result;
    private String kind;
    private String message;

    /*
    ** Constructor
     */
    public AskMessage(@NonNull Map<Value, Value> message) {
        super(message);
    }

    /*
    ** Life cycle
     */
    @Override
    protected void unSerialise(Map<Value, Value> message) {
        if (message.containsKey(ValueFactory.newString("type"))) {
            this.type = message.get(ValueFactory.newString("type")).asStringValue().asString();
        }
        if (message.containsKey(ValueFactory.newString("id"))) {
            this.message_id = message.get(ValueFactory.newString("id")).asStringValue().asString();
        }
        if (message.containsKey(ValueFactory.newString("result"))) {
            this.result = message.get(ValueFactory.newString("result")).asStringValue().asString();
        }
        if (message.containsKey(ValueFactory.newString("kind"))) {
            this.kind = message.get(ValueFactory.newString("kind")).asStringValue().asString();
        }
        if (message.containsKey(ValueFactory.newString("message"))) {
            this.message_id = message.get(ValueFactory.newString("message")).asStringValue().asString();
        }
    }
}
