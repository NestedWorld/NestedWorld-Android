package com.nestedworld.nestedworld.events.socket.base;

import android.support.annotation.NonNull;

import com.nestedworld.nestedworld.data.network.socket.models.message.DefaultMessage;

public abstract class SocketMessageEvent<T extends DefaultMessage> {
    private final T mMessage;

    /*
     * #############################################################################################
     * # Constructor
     * #############################################################################################
     */
    protected SocketMessageEvent(@NonNull final T message) {
        mMessage = message;
    }

    /*
     * #############################################################################################
     * # Public method
     * #############################################################################################
     */
    @NonNull
    public T getMessage() {
        return mMessage;
    }

}
