package com.nestedworld.nestedworld.network.socket.models.request.auth;

import android.support.annotation.NonNull;

import com.nestedworld.nestedworld.network.socket.models.request.DefaultRequest;

import org.msgpack.value.ValueFactory;

public class AuthRequest implements DefaultRequest {

    private String token;

    public AuthRequest(@NonNull final String token) {
        this.token = token;
    }

    @Override
    public ValueFactory.MapBuilder serialise() {
        ValueFactory.MapBuilder mapBuilder = ValueFactory.newMapBuilder();
        mapBuilder.put(ValueFactory.newString("token"), ValueFactory.newString(token));
        return mapBuilder;
    }
}
