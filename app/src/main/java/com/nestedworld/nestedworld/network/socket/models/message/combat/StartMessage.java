package com.nestedworld.nestedworld.network.socket.models.message.combat;

import android.support.annotation.NonNull;

import com.nestedworld.nestedworld.network.socket.models.message.DefaultMessage;

import org.msgpack.value.Value;
import org.msgpack.value.ValueFactory;

import java.util.Map;

public class StartMessage extends DefaultMessage {

    public String type;
    public String id;
    public Integer combatId;
    public Player user;
    public Opponent opponent;
    public String combatType;
    public String env;
    public boolean first;

    /*
    ** Constructor
     */
    public StartMessage(@NonNull Map<Value, Value> message) {
        super(message);
    }

    /*
    ** Life cycle
     */
    @Override
    protected void unSerialise(@NonNull Map<Value, Value> message) {
        this.type = message.get(ValueFactory.newString("type")).asStringValue().asString();
        this.id = message.get(ValueFactory.newString("id")).asStringValue().asString();
        this.combatId = message.get(ValueFactory.newString("combat_id")).asIntegerValue().asInt();
        this.combatType = message.get(ValueFactory.newString("combat_type")).asStringValue().asString();
        this.env = message.get(ValueFactory.newString("env")).asStringValue().asString();
        this.first = message.get(ValueFactory.newString("first")).asBooleanValue().getBoolean();

        if (message.containsKey(ValueFactory.newString("user"))) {
            Map<Value, Value> userMap = message.get(ValueFactory.newString("user")).asMapValue().map();
            if (userMap != null) {
                this.user = new Player(userMap);
            }
        }

        if (message.containsKey(ValueFactory.newString("opponent"))) {
            Map<Value, Value> opponentMap = message.get(ValueFactory.newString("opponent")).asMapValue().map();
            if (opponentMap != null) {
                this.opponent = new Opponent(opponentMap);
            }
        }
    }

    /*
    ** Generated
     */
    @Override
    public String toString() {
        return "StartMessage{" +
                "combat_id=" + combatId +
                ", type='" + type + '\'' +
                ", id='" + id + '\'' +
                ", user=" + user +
                ", opponent=" + opponent +
                ", combat_type='" + combatType + '\'' +
                ", env='" + env + '\'' +
                ", first=" + first +
                '}';
    }

    /*
    ** Inner class (used for parsing)
     */
    public static class PlayerMonster extends DefaultMessage {
        public Integer id;
        public String name;
        public Integer monsterId;
        public Integer userMonsterId;
        public Integer hp;
        public Integer level;

        public PlayerMonster(@NonNull Map<Value, Value> message) {
            super(message);
        }

        @Override
        protected void unSerialise(@NonNull Map<Value, Value> message) {
            this.id = message.get(ValueFactory.newString("id")).asIntegerValue().asInt();
            this.name = message.get(ValueFactory.newString("name")).asStringValue().asString();
            this.monsterId = message.get(ValueFactory.newString("monster_id")).asIntegerValue().asInt();
            this.userMonsterId = message.get(ValueFactory.newString("user_monster_id")).asIntegerValue().asInt();
            this.hp = message.get(ValueFactory.newString("hp")).asIntegerValue().asInt();
            this.level = message.get(ValueFactory.newString("level")).asIntegerValue().asInt();
        }
    }

    public static class Player extends DefaultMessage {

        public PlayerMonster monster;

        public Player(@NonNull Map<Value, Value> message) {
            super(message);
        }

        @Override
        protected void unSerialise(@NonNull Map<Value, Value> message) {
            this.monster = new PlayerMonster(message.get(ValueFactory.newString("monster")).asMapValue().map());
        }
    }

    public static class Opponent extends DefaultMessage {
        public PlayerMonster monster;
        public Integer monsterCount;

        public Opponent(@NonNull Map<Value, Value> message) {
            super(message);
        }

        @Override
        protected void unSerialise(@NonNull Map<Value, Value> message) {
            this.monster = new PlayerMonster(message.get(ValueFactory.newString("monster")).asMapValue().map());
            this.monsterCount = message.get(ValueFactory.newString("monsters_count")).asIntegerValue().asInt();
        }
    }
}
