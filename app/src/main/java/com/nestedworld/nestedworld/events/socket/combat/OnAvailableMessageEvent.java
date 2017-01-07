package com.nestedworld.nestedworld.events.socket.combat;

import android.support.annotation.NonNull;

import com.nestedworld.nestedworld.data.network.socket.models.message.combat.AvailableMessage;
import com.nestedworld.nestedworld.events.socket.base.SocketMessageEvent;

public class OnAvailableMessageEvent extends SocketMessageEvent<AvailableMessage> {
    /*
     * #############################################################################################
     * # Constructor
     * #############################################################################################
     */
    public OnAvailableMessageEvent(@NonNull final AvailableMessage message) {
        super(message);
    }
}
