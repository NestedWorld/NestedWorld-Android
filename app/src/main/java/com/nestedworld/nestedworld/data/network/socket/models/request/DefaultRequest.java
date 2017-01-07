package com.nestedworld.nestedworld.data.network.socket.models.request;

import android.support.annotation.NonNull;

import org.msgpack.value.ValueFactory;

public interface DefaultRequest {
    @NonNull
    ValueFactory.MapBuilder serialise();
}