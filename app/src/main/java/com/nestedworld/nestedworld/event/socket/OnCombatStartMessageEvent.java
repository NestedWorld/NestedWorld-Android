package com.nestedworld.nestedworld.event.socket;

import android.support.annotation.NonNull;

import com.nestedworld.nestedworld.event.socket.base.SocketMessageEvent;
import com.nestedworld.nestedworld.network.socket.models.message.combat.StartMessage;

public class OnCombatStartMessageEvent extends SocketMessageEvent<StartMessage> {

    public OnCombatStartMessageEvent(@NonNull StartMessage message) {
        super(message);
    }
}
