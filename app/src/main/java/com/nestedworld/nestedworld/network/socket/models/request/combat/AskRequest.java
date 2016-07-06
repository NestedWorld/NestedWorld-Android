package com.nestedworld.nestedworld.network.socket.models.request.combat;

import android.support.annotation.NonNull;

import com.nestedworld.nestedworld.network.socket.models.request.DefaultRequest;

import org.msgpack.value.ValueFactory;

public class AskRequest implements DefaultRequest {

    private String opponentPseudo;

    public AskRequest(@NonNull final String opponentPseudo) {
        this.opponentPseudo = opponentPseudo;
    }

    @Override
    public ValueFactory.MapBuilder serialise() {
        ValueFactory.MapBuilder mapBuilder = ValueFactory.newMapBuilder();
        mapBuilder.put(ValueFactory.newString("opponent"), ValueFactory.newString(opponentPseudo));

        return mapBuilder;
    }
}
