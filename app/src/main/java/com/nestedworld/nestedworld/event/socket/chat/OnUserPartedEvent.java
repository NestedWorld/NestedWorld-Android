package com.nestedworld.nestedworld.event.socket.chat;

import android.support.annotation.NonNull;

import com.nestedworld.nestedworld.event.socket.base.SocketMessageEvent;
import com.nestedworld.nestedworld.network.socket.models.message.message.UserPartedMessage;

public class OnUserPartedEvent extends SocketMessageEvent<UserPartedMessage> {

    public OnUserPartedEvent(@NonNull UserPartedMessage message) {
        super(message);
    }
}
