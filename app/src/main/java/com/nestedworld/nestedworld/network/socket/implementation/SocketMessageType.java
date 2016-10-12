package com.nestedworld.nestedworld.network.socket.implementation;

import com.nestedworld.nestedworld.helpers.bimap.BiMap;

public final class SocketMessageType {

    public static final BiMap<MessageKind, String> MESSAGE_TYPE = createMap();

    private SocketMessageType() {
        //Empty constructor for avoiding this class to be construct
    }

    private static BiMap<MessageKind, String> createMap() {
        BiMap<MessageKind, String> map = new BiMap<>();

         /*
        ** Spontaneous message (server side)
        ** Key = request.type
        ** -> we'll use the key for message parsing
         */
        //Chat
        map.put(MessageKind.TYPE_CHAT_USER_JOINED, "chat:user-joined");
        map.put(MessageKind.TYPE_CHAT_USER_PARTED, "chat:user-parted");
        map.put(MessageKind.TYPE_CHAT_MESSAGE_RECEIVED, "chat:message-received");

        //Combat
        map.put(MessageKind.TYPE_COMBAT_START, "combat:start");
        map.put(MessageKind.TYPE_COMBAT_AVAILABLE, "combat:available");
        map.put(MessageKind.TYPE_COMBAT_MONSTER_KO, "combat:monster-ko");
        map.put(MessageKind.TYPE_COMBAT_ATTACK_RECEIVED, "combat:attack-received");
        map.put(MessageKind.TYPE_COMBAT_MONSTER_REPLACED, "combat:monster-replaced");
        map.put(MessageKind.TYPE_COMBAT_END, "combat:end");

        //Geo
        map.put(MessageKind.TYPE_GEO_PLACES_CAPTURED, "geo:places:place-captured");

            /*
            ** Message send by app (client)
            ** Value = request.id
            ** -> we'll use the value to parse some response
             */
        //Auth
        map.put(MessageKind.TYPE_AUTHENTICATE, "authenticate");

        //Chat
        map.put(MessageKind.TYPE_CHAT_JOIN_CHANNEL, "chat:join-channel");
        map.put(MessageKind.TYPE_CHAT_PART_CHANNEL, "chat:part-channel");
        map.put(MessageKind.TYPE_CHAT_SEND_MESSAGE, "chat:send-message");


        //Combat
        map.put(MessageKind.TYPE_COMBAT_SEND_ATTACK, "combat:send-attack");
        map.put(MessageKind.TYPE_COMBAT_MONSTER_KO_CAPTURE, "combat:monster-ko:capture");
        map.put(MessageKind.TYPE_COMBAT_MONSTER_KO_REPLACE, "combat:monster-ko:replace");
        map.put(MessageKind.TYPE_COMBAT_FLEE, "combat:flee");
        map.put(MessageKind.TYPE_COMBAT_ASK, "combat:ask");

        /*
        ** Message send by app and by client
         */
        map.put(MessageKind.TYPE_RESULT, "result");

        return map;
    }

    //Enum use for listing every message (send & response)
    public enum MessageKind {
        //Server side message
        TYPE_CHAT_USER_JOINED,
        TYPE_CHAT_USER_PARTED,
        TYPE_CHAT_MESSAGE_RECEIVED,
        TYPE_COMBAT_START,
        TYPE_COMBAT_AVAILABLE,
        TYPE_COMBAT_MONSTER_KO,
        TYPE_COMBAT_ATTACK_RECEIVED,
        TYPE_COMBAT_MONSTER_REPLACED,
        TYPE_COMBAT_END,
        TYPE_GEO_PLACES_CAPTURED,

        //Client side message
        TYPE_AUTHENTICATE,
        TYPE_CHAT_JOIN_CHANNEL,
        TYPE_CHAT_PART_CHANNEL,
        TYPE_CHAT_SEND_MESSAGE,
        TYPE_COMBAT_SEND_ATTACK,
        TYPE_COMBAT_MONSTER_KO_CAPTURE,
        TYPE_COMBAT_MONSTER_KO_REPLACE,
        TYPE_COMBAT_FLEE,
        TYPE_COMBAT_ASK,

        //Client and Server
        TYPE_RESULT
    }
}
