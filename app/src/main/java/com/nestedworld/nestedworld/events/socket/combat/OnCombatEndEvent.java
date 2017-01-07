package com.nestedworld.nestedworld.events.socket.combat;

import android.support.annotation.NonNull;

import com.nestedworld.nestedworld.data.network.socket.models.message.combat.CombatEndMessage;
import com.nestedworld.nestedworld.events.socket.base.SocketMessageEvent;

public class OnCombatEndEvent extends SocketMessageEvent<CombatEndMessage> {
    /*
     * #############################################################################################
     * # Constructor
     * #############################################################################################
     */
    public OnCombatEndEvent(@NonNull final CombatEndMessage message) {
        super(message);
    }
}
