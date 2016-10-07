package com.nestedworld.nestedworld.events.socket.combat;

import android.support.annotation.NonNull;

import com.nestedworld.nestedworld.events.socket.base.SocketMessageEvent;
import com.nestedworld.nestedworld.network.socket.models.message.combat.AskMessage;

public class OnAskMessageEvent extends SocketMessageEvent<AskMessage> {

    public OnAskMessageEvent(@NonNull AskMessage message) {
        super(message);
    }
}
