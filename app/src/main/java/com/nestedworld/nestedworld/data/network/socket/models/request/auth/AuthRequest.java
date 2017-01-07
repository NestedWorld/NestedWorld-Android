package com.nestedworld.nestedworld.data.network.socket.models.request.auth;

import android.support.annotation.NonNull;

import com.nestedworld.nestedworld.data.network.socket.models.request.DefaultRequest;

import org.msgpack.value.ValueFactory;

public class AuthRequest implements DefaultRequest {

    private final String token;

    public AuthRequest(@NonNull final String token) {
        this.token = token;
    }

    @NonNull
    @Override
    public ValueFactory.MapBuilder serialise() {
        ValueFactory.MapBuilder mapBuilder = ValueFactory.newMapBuilder();
        mapBuilder.put(ValueFactory.newString("token"), ValueFactory.newString(token));
        return mapBuilder;
    }
}
