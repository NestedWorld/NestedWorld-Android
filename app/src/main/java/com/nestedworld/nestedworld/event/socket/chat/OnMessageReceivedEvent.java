package com.nestedworld.nestedworld.event.socket.chat;

import android.support.annotation.NonNull;

import com.nestedworld.nestedworld.event.socket.base.SocketMessageEvent;
import com.nestedworld.nestedworld.network.socket.models.message.message.MessageReceivedMessage;


public class OnMessageReceivedEvent extends SocketMessageEvent<MessageReceivedMessage> {

    public OnMessageReceivedEvent(@NonNull MessageReceivedMessage message) {
        super(message);
    }
}
