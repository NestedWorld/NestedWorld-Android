package com.nestedworld.nestedworld.network.socket.models.message;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.nestedworld.nestedworld.network.socket.implementation.SocketMessageType;

import org.msgpack.value.Value;

import java.util.Map;

public abstract class DefaultMessage {

    protected final SocketMessageType.MessageKind mMessageKind;
    protected final SocketMessageType.MessageKind mIdKind;

    /*
    ** Constructor
     */
    public DefaultMessage(@NonNull final Map<Value, Value> message, @NonNull final SocketMessageType.MessageKind messageKind, @Nullable final SocketMessageType.MessageKind idKind) {
        mMessageKind = messageKind;
        mIdKind = idKind;
        this.unSerialise(message);
    }

    /*
    ** Child method
     */
    protected abstract void unSerialise(@NonNull final Map<Value, Value> message);

    /*
    ** Utils
     */
    protected SocketMessageType.MessageKind getIdKind() {
        return mIdKind;
    }

    protected SocketMessageType.MessageKind getMessageKind() {
        return mMessageKind;
    }
}
