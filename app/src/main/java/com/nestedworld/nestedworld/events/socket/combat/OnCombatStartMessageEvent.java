package com.nestedworld.nestedworld.events.socket.combat;

import android.support.annotation.NonNull;

import com.nestedworld.nestedworld.data.network.socket.models.message.combat.StartMessage;
import com.nestedworld.nestedworld.events.socket.base.SocketMessageEvent;

public class OnCombatStartMessageEvent extends SocketMessageEvent<StartMessage> {
    /*
     * #############################################################################################
     * # Constructor
     * #############################################################################################
     */
    public OnCombatStartMessageEvent(@NonNull final StartMessage message) {
        super(message);
    }
}
