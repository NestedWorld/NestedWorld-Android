package com.nestedworld.nestedworld.events.socket.chat;

import android.support.annotation.NonNull;

import com.nestedworld.nestedworld.events.socket.base.SocketMessageEvent;
import com.nestedworld.nestedworld.network.socket.models.message.message.UserJoinedMessage;

public class OnUserJoinedEvent extends SocketMessageEvent<UserJoinedMessage> {

    public OnUserJoinedEvent(@NonNull UserJoinedMessage message) {
        super(message);
    }
}
