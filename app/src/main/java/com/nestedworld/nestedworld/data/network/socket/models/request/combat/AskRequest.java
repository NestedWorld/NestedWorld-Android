package com.nestedworld.nestedworld.data.network.socket.models.request.combat;

import android.support.annotation.NonNull;

import com.nestedworld.nestedworld.data.network.socket.models.request.DefaultRequest;

import org.msgpack.value.ValueFactory;

public class AskRequest implements DefaultRequest {

    private final String opponentPseudo;

    public AskRequest(@NonNull final String opponentPseudo) {
        this.opponentPseudo = opponentPseudo;
    }

    @NonNull
    @Override
    public ValueFactory.MapBuilder serialise() {
        ValueFactory.MapBuilder mapBuilder = ValueFactory.newMapBuilder();
        mapBuilder.put(ValueFactory.newString("opponent"), ValueFactory.newString(opponentPseudo));

        return mapBuilder;
    }
}
