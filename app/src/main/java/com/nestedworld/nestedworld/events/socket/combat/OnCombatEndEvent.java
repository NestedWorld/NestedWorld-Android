package com.nestedworld.nestedworld.events.socket.combat;

import android.support.annotation.NonNull;

import com.nestedworld.nestedworld.events.socket.base.SocketMessageEvent;
import com.nestedworld.nestedworld.network.socket.models.message.combat.CombatEndMessage;

public class OnCombatEndEvent extends SocketMessageEvent<CombatEndMessage> {

    public OnCombatEndEvent(@NonNull CombatEndMessage message) {
        super(message);
    }
}
