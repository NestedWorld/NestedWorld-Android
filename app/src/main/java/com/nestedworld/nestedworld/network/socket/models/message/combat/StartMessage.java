package com.nestedworld.nestedworld.network.socket.models.message.combat;

import android.support.annotation.NonNull;

import com.nestedworld.nestedworld.network.socket.models.message.DefaultMessage;

import org.msgpack.value.Value;
import org.msgpack.value.ValueFactory;

import java.util.Map;

public class StartMessage implements DefaultMessage {

    public static class PlayerMonster {
        Integer id;
        String name;
        Integer monster_id;
        Integer user_monster_id;
        Integer hp;
        Integer level;

        public static PlayerMonster fromMessage(@NonNull final Map<Value, Value> combatOpponentMonsterMap) {
            PlayerMonster combatOpponentMonster = new PlayerMonster();
            combatOpponentMonster.id = combatOpponentMonsterMap.get(ValueFactory.newString("id")).asIntegerValue().asInt();
            combatOpponentMonster.name = combatOpponentMonsterMap.get(ValueFactory.newString("name")).asStringValue().asString();
            combatOpponentMonster.monster_id = combatOpponentMonsterMap.get(ValueFactory.newString("monster_id")).asIntegerValue().asInt();
            combatOpponentMonster.user_monster_id = combatOpponentMonsterMap.get(ValueFactory.newString("user_monster_id")).asIntegerValue().asInt();
            combatOpponentMonster.hp = combatOpponentMonsterMap.get(ValueFactory.newString("hp")).asIntegerValue().asInt();
            combatOpponentMonster.level = combatOpponentMonsterMap.get(ValueFactory.newString("level")).asIntegerValue().asInt();

            return combatOpponentMonster;
        }
    }

    public static class Player {
        PlayerMonster monster;

        public static Player fromMessage(@NonNull final Map<Value, Value> playerMap) {
            Player player = new Player();
            player.monster = PlayerMonster.fromMessage(playerMap.get(ValueFactory.newString("monster")).asMapValue().map());

            return player;
        }
    }

    public static class Opponent {
        PlayerMonster monster;
        Integer monster_count;

        public static Opponent fromMessage(@NonNull final Map<Value, Value> opponentMap) {
            Opponent opponent = new Opponent();
            opponent.monster = PlayerMonster.fromMessage(opponentMap.get(ValueFactory.newString("monster")).asMapValue().map());

            opponent.monster_count = opponentMap.get(ValueFactory.newString("monsters_count")).asIntegerValue().asInt();

            return opponent;
        }
    }

    String type;
    String id;
    Integer combat_id;
    Player user;
    Opponent opponent;
    String combat_type;
    String env;
    boolean first;

    @Override
    public void unSerialise(Map<Value, Value> message) {
        this.type = message.get(ValueFactory.newString("type")).asStringValue().asString();
        this.id = message.get(ValueFactory.newString("id")).asStringValue().asString();
        this.combat_id = message.get(ValueFactory.newString("combat_id")).asIntegerValue().asInt();
        this.combat_type = message.get(ValueFactory.newString("combat_type")).asStringValue().asString();
        this.env = message.get(ValueFactory.newString("env")).asStringValue().asString();
        this.first = message.get(ValueFactory.newString("first")).asBooleanValue().getBoolean();

        if (message.containsKey(ValueFactory.newString("user"))) {
            Map<Value, Value> userMap = message.get(ValueFactory.newString("user")).asMapValue().map();
            if (userMap != null) {
                this.user = Player.fromMessage(userMap);
            }
        }

        if (message.containsKey(ValueFactory.newString("opponent"))) {
            Map<Value, Value> opponentMap = message.get(ValueFactory.newString("opponent")).asMapValue().map();
            if (opponentMap != null) {
                this.opponent = Opponent.fromMessage(opponentMap);
            }
        }
    }
}
