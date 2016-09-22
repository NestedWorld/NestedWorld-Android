package com.nestedworld.nestedworld.network.socket.models.message.message;

import android.support.annotation.NonNull;

import com.nestedworld.nestedworld.network.socket.models.message.DefaultMessage;

import org.msgpack.value.Value;

import java.util.Map;

public class UserPartedMessage extends DefaultMessage {

    /*
    ** Constructor
     */
    public UserPartedMessage(@NonNull Map<Value, Value> message) {
        super(message);
    }

    /*
    ** Life cycle
     */
    @Override
    protected void unSerialise(Map<Value, Value> message) {

    }


}
