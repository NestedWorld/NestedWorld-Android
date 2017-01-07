package com.nestedworld.nestedworld.data.network.socket.models.request.combat;

import android.support.annotation.NonNull;

import com.nestedworld.nestedworld.data.network.socket.models.request.DefaultRequest;

import org.msgpack.value.ValueFactory;

public class ReplaceMonsterRequest implements DefaultRequest {

    private final long userMonsterId;
    private final long combatId;

    /*
    ** Constructor
     */
    public ReplaceMonsterRequest(final long userMonsterId, final long combatId) {
        this.userMonsterId = userMonsterId;
        this.combatId = combatId;
    }

    /*
    ** DefaultRequest implementation
     */
    @NonNull
    @Override
    public ValueFactory.MapBuilder serialise() {
        ValueFactory.MapBuilder mapBuilder = ValueFactory.newMapBuilder();
        mapBuilder.put(ValueFactory.newString("combat"), ValueFactory.newInteger(combatId));
        mapBuilder.put(ValueFactory.newString("user_monster_id"), ValueFactory.newInteger(userMonsterId));
        return mapBuilder;
    }
}
