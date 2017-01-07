package com.nestedworld.nestedworld.events.socket.generic;

import android.support.annotation.NonNull;

import com.nestedworld.nestedworld.data.network.socket.models.message.generic.ResultMessage;
import com.nestedworld.nestedworld.events.socket.base.SocketMessageEvent;

public class OnResultResponseEvent extends SocketMessageEvent<ResultMessage> {

    /*
    ** Constructor
     */
    public OnResultResponseEvent(@NonNull final ResultMessage message) {
        super(message);
    }
}
