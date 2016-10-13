package com.nestedworld.nestedworld.events.socket.generic;

import android.support.annotation.NonNull;

import com.nestedworld.nestedworld.events.socket.base.SocketMessageEvent;
import com.nestedworld.nestedworld.network.socket.models.message.generic.ResultMessage;

public class OnResultResponseEvent extends SocketMessageEvent<ResultMessage> {

    /*
    ** Constructor
     */
    public OnResultResponseEvent(@NonNull ResultMessage message) {
        super(message);
    }
}
