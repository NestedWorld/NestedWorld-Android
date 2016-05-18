package com.nestedworld.nestedworld.network.socket.models.request.chat;


import com.nestedworld.nestedworld.network.socket.models.DefaultModel;

import org.msgpack.value.ValueFactory;

public class JoinChannelRequest implements DefaultModel {
    public String channel;

    @Override
    public ValueFactory.MapBuilder serialise() {
        ValueFactory.MapBuilder mapBuilder = ValueFactory.newMapBuilder();
        mapBuilder.put(ValueFactory.newString("channel"), ValueFactory.newString(channel));

        return mapBuilder;
    }
}
