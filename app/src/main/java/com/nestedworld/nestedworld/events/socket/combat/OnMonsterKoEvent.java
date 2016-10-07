package com.nestedworld.nestedworld.events.socket.combat;

import android.support.annotation.NonNull;

import com.nestedworld.nestedworld.events.socket.base.SocketMessageEvent;
import com.nestedworld.nestedworld.network.socket.models.message.combat.MonsterKoMessage;

public class OnMonsterKoEvent extends SocketMessageEvent<MonsterKoMessage> {

    public OnMonsterKoEvent(@NonNull MonsterKoMessage message) {
        super(message);
    }
}
