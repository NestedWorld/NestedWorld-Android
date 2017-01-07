package com.nestedworld.nestedworld.events.socket.chat;

import android.support.annotation.NonNull;

import com.nestedworld.nestedworld.data.network.socket.models.message.message.UserPartedMessage;
import com.nestedworld.nestedworld.events.socket.base.SocketMessageEvent;

public class OnUserPartedEvent extends SocketMessageEvent<UserPartedMessage> {

    public OnUserPartedEvent(@NonNull final UserPartedMessage message) {
        super(message);
    }
}
