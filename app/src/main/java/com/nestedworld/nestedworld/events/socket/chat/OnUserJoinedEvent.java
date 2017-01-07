package com.nestedworld.nestedworld.events.socket.chat;

import android.support.annotation.NonNull;

import com.nestedworld.nestedworld.data.network.socket.models.message.message.UserJoinedMessage;
import com.nestedworld.nestedworld.events.socket.base.SocketMessageEvent;

public class OnUserJoinedEvent extends SocketMessageEvent<UserJoinedMessage> {
    /*
     * #############################################################################################
     * # Constructor
     * #############################################################################################
     */
    public OnUserJoinedEvent(@NonNull final UserJoinedMessage message) {
        super(message);
    }
}
