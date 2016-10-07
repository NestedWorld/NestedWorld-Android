package com.nestedworld.nestedworld.events.socket.base;

import android.support.annotation.NonNull;

import com.nestedworld.nestedworld.network.socket.models.message.DefaultMessage;

public abstract class SocketMessageEvent<T extends DefaultMessage> {
    private final T mMessage;

    /*
    ** Constructor
     */
    public SocketMessageEvent(@NonNull final T message) {
        mMessage = message;
    }

    /*
    ** public method
     */
    public T getMessage() {
        return mMessage;
    }

}
