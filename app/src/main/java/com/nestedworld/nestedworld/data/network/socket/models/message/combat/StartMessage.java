package com.nestedworld.nestedworld.data.network.socket.models.message.combat;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.nestedworld.nestedworld.data.database.entities.Monster;
import com.nestedworld.nestedworld.data.database.entities.MonsterDao;
import com.nestedworld.nestedworld.data.database.implementation.NestedWorldDatabase;
import com.nestedworld.nestedworld.data.network.socket.implementation.SocketMessageType;
import com.nestedworld.nestedworld.data.network.socket.models.message.DefaultMessage;

import org.msgpack.value.Value;
import org.msgpack.value.ValueFactory;

import java.util.Map;

public class StartMessage extends DefaultMessage {

    public String type;
    public String id;
    public long combatId;
    public StartMessagePlayer user;
    public StartMessageOpponent opponent;
    public String combatType;
    public String env;
    public boolean first;

    /*
    ** Constructor
     */
    public StartMessage(@NonNull Map<Value, Value> message,
                        @NonNull SocketMessageType.MessageKind messageKind,
                        @Nullable SocketMessageType.MessageKind idKind) {
        super(message, messageKind, idKind);
    }

    /*
    ** Life cycle
     */
    @Override
    protected void unSerialise(@NonNull Map<Value, Value> message) {
        this.type = message.get(ValueFactory.newString("type")).asStringValue().asString();
        this.id = message.get(ValueFactory.newString("id")).asStringValue().asString();
        this.combatId = message.get(ValueFactory.newString("combat_id")).asIntegerValue().asLong();
        this.combatType = message.get(ValueFactory.newString("combat_type")).asStringValue().asString();
        this.env = message.get(ValueFactory.newString("env")).asStringValue().asString();
        this.first = message.get(ValueFactory.newString("first")).asBooleanValue().getBoolean();

        if (message.containsKey(ValueFactory.newString("user"))) {
            Map<Value, Value> userMap = message.get(ValueFactory.newString("user")).asMapValue().map();
            if (userMap != null) {
                this.user = new StartMessagePlayer(userMap, getMessageKind(), null);
            }
        }

        if (message.containsKey(ValueFactory.newString("opponent"))) {
            Map<Value, Value> opponentMap = message.get(ValueFactory.newString("opponent")).asMapValue().map();
            if (opponentMap != null) {
                this.opponent = new StartMessageOpponent(opponentMap, getMessageKind(), null);
            }
        }
    }

    /*
    ** Generated
     */
    @Override
    public String toString() {
        return "StartMessage{" +
                "combatId=" + combatId +
                ", type='" + type + '\'' +
                ", id='" + id + '\'' +
                ", player=" + user +
                ", opponent=" + opponent +
                ", combat_type='" + combatType + '\'' +
                ", env='" + env + '\'' +
                ", first=" + first +
                '}';
    }

    /*
    ** Inner class (used for parsing)
     */
    public static class StartMessagePlayer extends DefaultMessage {

        public StartMessagePlayerMonster monster;

        public StartMessagePlayer(@NonNull Map<Value, Value> message, @NonNull SocketMessageType.MessageKind messageKind, @Nullable SocketMessageType.MessageKind idKind) {
            super(message, messageKind, idKind);
        }

        @Override
        protected void unSerialise(@NonNull Map<Value, Value> message) {
            this.monster = new StartMessagePlayerMonster(message.get(ValueFactory.newString("monster")).asMapValue().map(), getMessageKind(), null);
        }
    }

    public static class StartMessageOpponent extends DefaultMessage {
        public StartMessagePlayerMonster monster;
        public int monsterCount;

        /*
        ** Constructor
         */
        public StartMessageOpponent(@NonNull Map<Value, Value> message, @NonNull SocketMessageType.MessageKind messageKind, @Nullable SocketMessageType.MessageKind idKind) {
            super(message, messageKind, idKind);
        }

        /*
        ** Life cycle
         */
        @Override
        protected void unSerialise(@NonNull Map<Value, Value> message) {
            this.monster = new StartMessagePlayerMonster(message.get(ValueFactory.newString("monster")).asMapValue().map(), getMessageKind(), null);
            this.monsterCount = message.get(ValueFactory.newString("monsters_count")).asIntegerValue().asInt();
        }
    }

    public static class StartMessagePlayerMonster extends DefaultMessage {
        public long id;
        public String name;
        public long monsterId;
        public long userMonsterId;
        public int hp;
        public int level;

        /*
        ** Constructor
         */
        public StartMessagePlayerMonster(@NonNull Map<Value, Value> message, @NonNull SocketMessageType.MessageKind messageKind, @Nullable SocketMessageType.MessageKind idKind) {
            super(message, messageKind, idKind);
        }

        /*
        ** Utils
         */
        @Nullable
        public Monster info() {
            return NestedWorldDatabase.getInstance()
                    .getDataBase()
                    .getMonsterDao()
                    .queryBuilder()
                    .where(MonsterDao.Properties.MonsterId.eq(monsterId))
                    .unique();
        }

        /*
        ** Life cycle
         */
        @Override
        protected void unSerialise(@NonNull Map<Value, Value> message) {
            //non nullable field
            this.id = message.get(ValueFactory.newString("id")).asIntegerValue().asLong();
            this.name = message.get(ValueFactory.newString("name")).asStringValue().asString();
            this.monsterId = message.get(ValueFactory.newString("monster_id")).asIntegerValue().asLong();
            this.hp = message.get(ValueFactory.newString("hp")).asIntegerValue().asInt();
            this.level = message.get(ValueFactory.newString("level")).asIntegerValue().asInt();

            //nullable field
            Value rawUserMonsterId = message.get(ValueFactory.newString("user_monster_id"));
            if (rawUserMonsterId != null) {
                this.userMonsterId = -1;
            }
        }

        /*
        ** Generated
         */
        @Override
        public String toString() {
            return "StartMessagePlayerMonster{" +
                    "hp=" + hp +
                    ", id=" + id +
                    ", name='" + name + '\'' +
                    ", monsterId=" + monsterId +
                    ", userMonsterId=" + userMonsterId +
                    ", level=" + level +
                    '}';
        }
    }
}
