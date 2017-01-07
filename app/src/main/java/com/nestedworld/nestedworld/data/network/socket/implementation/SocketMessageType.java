package com.nestedworld.nestedworld.data.network.socket.implementation;

import com.nestedworld.nestedworld.helpers.bimap.BiMap;

public final class SocketMessageType {

    public final static BiMap<MessageKind, String> MESSAGE_TYPE = new BiMap<MessageKind, String>() {{
        /*
        ** Spontaneous message (server side)
        ** Key = request.type
        ** -> we'll use the key for message parsing
         */
        //Chat
        put(MessageKind.TYPE_CHAT_USER_JOINED, "chat:player-joined");
        put(MessageKind.TYPE_CHAT_USER_PARTED, "chat:player-parted");
        put(MessageKind.TYPE_CHAT_MESSAGE_RECEIVED, "chat:message-received");

        //Combat
        put(MessageKind.TYPE_COMBAT_START, "combat:start");
        put(MessageKind.TYPE_COMBAT_AVAILABLE, "combat:available");
        put(MessageKind.TYPE_COMBAT_MONSTER_KO, "combat:monster-ko");
        put(MessageKind.TYPE_COMBAT_ATTACK_RECEIVED, "combat:attack-received");
        put(MessageKind.TYPE_COMBAT_MONSTER_REPLACED, "combat:monster-replaced");
        put(MessageKind.TYPE_COMBAT_END, "combat:end");

        //Geo
        put(MessageKind.TYPE_GEO_PLACES_CAPTURED, "geo:places:place-captured");

        /*
        ** Message send by app (client)
        ** Value = request.id
        ** -> we'll use the value to parse some response
         */
        //Auth
        put(MessageKind.TYPE_AUTHENTICATE, "authenticate");

        //Chat
        put(MessageKind.TYPE_CHAT_JOIN_CHANNEL, "chat:join-channel");
        put(MessageKind.TYPE_CHAT_PART_CHANNEL, "chat:part-channel");
        put(MessageKind.TYPE_CHAT_SEND_MESSAGE, "chat:send-message");

        //Combat
        put(MessageKind.TYPE_COMBAT_SEND_ATTACK, "combat:send-attack");
        put(MessageKind.TYPE_COMBAT_MONSTER_KO_CAPTURE, "combat:monster-ko:capture");
        put(MessageKind.TYPE_COMBAT_MONSTER_KO_REPLACE, "combat:monster-ko:replace");
        put(MessageKind.TYPE_COMBAT_FLEE, "combat:flee");
        put(MessageKind.TYPE_COMBAT_ASK, "combat:ask");

        /*
        ** Message send by app and by client
         */
        put(MessageKind.TYPE_RESULT, "result");
    }};

    /*
     * #############################################################################################
     * # Constructor
     * #############################################################################################
     */
    private SocketMessageType() {
        //Private constructor for avoiding this class to be construct
    }

    /**
     * used for listing every message (send & response)
     */
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
