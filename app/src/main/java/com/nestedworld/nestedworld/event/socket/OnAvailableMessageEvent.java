package com.nestedworld.nestedworld.event.socket;

import android.support.annotation.NonNull;

import com.nestedworld.nestedworld.event.socket.base.SocketMessageEvent;
import com.nestedworld.nestedworld.network.socket.models.message.combat.AvailableMessage;

public class OnAvailableMessageEvent extends SocketMessageEvent<AvailableMessage> {

    public OnAvailableMessageEvent(@NonNull AvailableMessage message) {
        super(message);
    }
}
