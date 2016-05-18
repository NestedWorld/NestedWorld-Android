package com.nestedworld.nestedworld.network.socket.models.request.chat;

import com.nestedworld.nestedworld.network.socket.models.DefaultModel;

import org.msgpack.value.ValueFactory;

public class SendMessageRequest implements DefaultModel {
    public String channel;
    public String message;

    @Override
    public ValueFactory.MapBuilder serialise() {
        ValueFactory.MapBuilder mapBuilder = ValueFactory.newMapBuilder();
        mapBuilder.put(ValueFactory.newString("channel"), ValueFactory.newString(channel));
        mapBuilder.put(ValueFactory.newString("message"), ValueFactory.newString(message));

        return mapBuilder;
    }
}
