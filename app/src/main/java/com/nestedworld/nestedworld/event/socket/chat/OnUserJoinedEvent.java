package com.nestedworld.nestedworld.event.socket.chat;

import android.support.annotation.NonNull;

import com.nestedworld.nestedworld.event.socket.base.SocketMessageEvent;
import com.nestedworld.nestedworld.network.socket.models.message.message.UserJoinedMessage;

public class OnUserJoinedEvent extends SocketMessageEvent<UserJoinedMessage> {

    public OnUserJoinedEvent(@NonNull UserJoinedMessage message) {
        super(message);
    }
}
