package com.nestedworld.nestedworld.events.socket.chat;

import android.support.annotation.NonNull;

import com.nestedworld.nestedworld.events.socket.base.SocketMessageEvent;
import com.nestedworld.nestedworld.network.socket.models.message.message.MessageReceivedMessage;


public class OnMessageReceivedEvent extends SocketMessageEvent<MessageReceivedMessage> {

    public OnMessageReceivedEvent(@NonNull final MessageReceivedMessage message) {
        super(message);
    }
}