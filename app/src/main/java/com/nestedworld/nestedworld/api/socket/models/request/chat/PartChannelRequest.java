package com.nestedworld.nestedworld.api.socket.models.request.chat;


import com.nestedworld.nestedworld.api.socket.models.DefaultModel;

import org.msgpack.value.ValueFactory;

public class PartChannelRequest implements DefaultModel {

    public String channel;

    @Override
    public ValueFactory.MapBuilder serialise() {
        ValueFactory.MapBuilder mapBuilder = ValueFactory.newMapBuilder();
        mapBuilder.put(ValueFactory.newString("channel"), ValueFactory.newString(channel));

        return mapBuilder;
    }
}
