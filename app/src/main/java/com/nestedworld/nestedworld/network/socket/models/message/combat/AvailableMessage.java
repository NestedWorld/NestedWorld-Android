package com.nestedworld.nestedworld.network.socket.models.message.combat;

import com.nestedworld.nestedworld.models.Combat;
import com.nestedworld.nestedworld.network.socket.models.message.DefaultMessage;

import org.msgpack.value.Value;
import org.msgpack.value.ValueFactory;

import java.util.Map;

public class AvailableMessage implements DefaultMessage {

    public String type;
    public String id;
    public String origin;
    public Integer monsterId;
    public String opponentName;

    @Override
    public void unSerialise(Map<Value, Value> message) {
        Combat combat = new Combat();

        if (message.containsKey(ValueFactory.newString("type"))) {
            type = combat.type = message.get(ValueFactory.newString("type")).asStringValue().asString();
        }
        if (message.containsKey(ValueFactory.newString("id"))) {
            id = combat.message_id = message.get(ValueFactory.newString("id")).asStringValue().asString();
        }
        if (message.containsKey(ValueFactory.newString("origin"))) {
            origin = combat.origin = message.get(ValueFactory.newString("origin")).asStringValue().asString();
        }
        if (message.containsKey(ValueFactory.newString("monster_id"))) {
            monsterId = combat.monsterId = message.get(ValueFactory.newString("monster_id")).asIntegerValue().asInt();
        }
        if (message.containsKey(ValueFactory.newString("user"))) {
            Map<Value, Value> userInfo = message.get(ValueFactory.newString("user")).asMapValue().map();
            opponentName = combat.opponent_pseudo = userInfo.get(ValueFactory.newString("pseudo")).asStringValue().asString();
        }
        combat.save();
    }
}
