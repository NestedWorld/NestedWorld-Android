package com.nestedworld.nestedworld.events.socket.combat;

import android.support.annotation.NonNull;

import com.nestedworld.nestedworld.events.socket.base.SocketMessageEvent;
import com.nestedworld.nestedworld.network.socket.models.message.combat.StartMessage;

public class OnCombatStartMessageEvent extends SocketMessageEvent<StartMessage> {

    public OnCombatStartMessageEvent(@NonNull StartMessage message) {
        super(message);
    }
}
