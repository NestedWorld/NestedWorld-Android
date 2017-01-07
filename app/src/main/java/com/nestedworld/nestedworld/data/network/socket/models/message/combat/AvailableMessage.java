package com.nestedworld.nestedworld.data.network.socket.models.message.combat;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.nestedworld.nestedworld.data.database.implementation.NestedWorldDatabase;
import com.nestedworld.nestedworld.data.database.entities.Combat;
import com.nestedworld.nestedworld.data.network.socket.implementation.SocketMessageType;
import com.nestedworld.nestedworld.data.network.socket.models.message.DefaultMessage;

import org.msgpack.value.Value;
import org.msgpack.value.ValueFactory;

import java.util.Map;

public class AvailableMessage extends DefaultMessage {

    public String combatId;
    private String type;
    private String origin;
    private long monsterId;
    private String opponentPseudo;

    /*
    ** Constructor
     */
    public AvailableMessage(@NonNull Map<Value, Value> message,
                            @NonNull SocketMessageType.MessageKind messageKind,
                            @Nullable SocketMessageType.MessageKind idKind) {
        super(message, messageKind, idKind);
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

    /*
    ** Utils
     */
    public Combat saveAsCombat() {
        Combat combat = new Combat();
        combat.type = this.type;
        combat.combatId = this.combatId;
        combat.origin = this.origin;
        combat.monsterId = this.monsterId;
        combat.opponentPseudo = this.opponentPseudo;

        NestedWorldDatabase.getInstance()
                .getDataBase()
                .getCombatDao()
                .insert(combat);

        return combat;
    }

    /*
    ** Getter (generated)
     */
    public String getMessageId() {
        return combatId;
    }
}
