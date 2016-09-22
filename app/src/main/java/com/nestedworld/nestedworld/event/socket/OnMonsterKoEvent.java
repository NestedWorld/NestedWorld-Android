package com.nestedworld.nestedworld.event.socket;

import android.support.annotation.NonNull;

import com.nestedworld.nestedworld.event.socket.base.SocketMessageEvent;
import com.nestedworld.nestedworld.network.socket.models.message.combat.MonsterKoMessage;

public class OnMonsterKoEvent extends SocketMessageEvent<MonsterKoMessage> {

    public OnMonsterKoEvent(@NonNull MonsterKoMessage message) {
        super(message);
    }
}
