package com.nestedworld.nestedworld.network.socket.models.request.result;

import android.support.annotation.NonNull;

import com.nestedworld.nestedworld.network.socket.models.request.DefaultRequest;

import org.msgpack.value.Value;
import org.msgpack.value.ValueFactory;

import java.util.Map;
import java.util.Objects;

public class ResultRequest implements DefaultRequest{

    private Map<Value, Value> data;

    public ResultRequest(@NonNull final Map<Value, Value> data) {
        this.data = data;
    }

    @Override
    public ValueFactory.MapBuilder serialise() {
        ValueFactory.MapBuilder mapBuilder = ValueFactory.newMapBuilder();

        for (Map.Entry<Value, Value> entry : data.entrySet()) {
            mapBuilder.put(entry.getKey(), entry.getValue());
        }

        mapBuilder.put(ValueFactory.newString("type"), ValueFactory.newString("result"));


        return mapBuilder;
    }
}
