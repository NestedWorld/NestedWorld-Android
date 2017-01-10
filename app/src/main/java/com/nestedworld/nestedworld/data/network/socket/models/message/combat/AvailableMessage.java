package com.nestedworld.nestedworld.data.network.socket.models.message.combat;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.nestedworld.nestedworld.data.database.entities.Combat;
import com.nestedworld.nestedworld.data.network.socket.implementation.SocketMessageType;
import com.nestedworld.nestedworld.data.network.socket.models.message.DefaultMessage;

import org.msgpack.value.Value;
import org.msgpack.value.ValueFactory;

import java.util.Map;

public class AvailableMessage extends DefaultMessage {

    public String combatId;
    public String type;
    public String origin;
    public long monsterId;
    public String opponentPseudo;

    /*
    ** Constructor
     */
    public AvailableMessage(@NonNull final Map<Value, Value> message,
                            @NonNull final SocketMessageType.MessageKind messageKind,
                            @Nullable final SocketMessageType.MessageKind idKind) {
        super(message, messageKind, idKind);
    }

    /*
    ** Getter (generated)
     */
    public String getMessageId() {
        return combatId;
    }

    /*
    ** Life cycle
     */
    @Override
    protected void unSerialise(@NonNull Map<Value, Value> message) {
        Combat combat = new Combat();

        if (message.containsKey(ValueFactory.newString("type"))) {
            this.type = combat.type = message.get(ValueFactory.newString("type")).asStringValue().asString();
        }
        if (message.containsKey(ValueFactory.newString("id"))) {
            this.combatId = combat.combatId = message.get(ValueFactory.newString("id")).asStringValue().asString();
        }
        if (message.containsKey(ValueFactory.newString("origin"))) {
            this.origin = combat.origin = message.get(ValueFactory.newString("origin")).asStringValue().asString();
        }
        if (message.containsKey(ValueFactory.newString("monster_id"))) {
            this.monsterId = combat.monsterId = message.get(ValueFactory.newString("monster_id")).asIntegerValue().asLong();
        }
        if (message.containsKey(ValueFactory.newString("player"))) {
            Map<Value, Value> userInfo = message.get(ValueFactory.newString("player")).asMapValue().map();
            this.opponentPseudo = combat.opponentPseudo = userInfo.get(ValueFactory.newString("pseudo")).asStringValue().asString();
        }
    }
}
