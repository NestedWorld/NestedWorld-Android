package com.nestedworld.nestedworld.events.socket.combat;

import android.support.annotation.NonNull;

import com.nestedworld.nestedworld.data.network.socket.models.message.combat.MonsterKoMessage;
import com.nestedworld.nestedworld.events.socket.base.SocketMessageEvent;

public class OnMonsterKoEvent extends SocketMessageEvent<MonsterKoMessage> {

    public OnMonsterKoEvent(@NonNull final MonsterKoMessage message) {
        super(message);
    }
}
