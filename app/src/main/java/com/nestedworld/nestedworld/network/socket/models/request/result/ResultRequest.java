package com.nestedworld.nestedworld.network.socket.models.request.result;

import android.support.annotation.NonNull;

import com.nestedworld.nestedworld.network.socket.models.request.DefaultRequest;

import org.msgpack.value.Value;
import org.msgpack.value.ValueFactory;

import java.util.Map;

public class ResultRequest implements DefaultRequest {

    private final Map<Value, Value> data;
    private final boolean success;

    public ResultRequest(@NonNull final Map<Value, Value> data, final boolean success) {
        this.data = data;
        this.success = success;
    }

    @Override
    public ValueFactory.MapBuilder serialise() {
        ValueFactory.MapBuilder mapBuilder = ValueFactory.newMapBuilder();

        for (Map.Entry<Value, Value> entry : data.entrySet()) {
            mapBuilder.put(entry.getKey(), entry.getValue());
        }

        mapBuilder.put(ValueFactory.newString("type"), ValueFactory.newString("result"));
        mapBuilder.put(ValueFactory.newString("result"), ValueFactory.newString(success ? "success" : "error"));

        return mapBuilder;
    }
}
