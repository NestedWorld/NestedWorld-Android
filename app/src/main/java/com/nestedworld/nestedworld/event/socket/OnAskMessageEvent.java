package com.nestedworld.nestedworld.event.socket;

import android.support.annotation.NonNull;

import com.nestedworld.nestedworld.event.socket.base.SocketMessageEvent;
import com.nestedworld.nestedworld.network.socket.models.message.combat.AskMessage;

public class OnAskMessageEvent extends SocketMessageEvent<AskMessage> {

    public OnAskMessageEvent(@NonNull AskMessage message) {
        super(message);
    }
}
