package com.nestedworld.nestedworld.api.socket.listener;

import android.support.annotation.NonNull;

import com.nestedworld.nestedworld.api.socket.implementation.NestedWorldSocketAPI;

public abstract class ConnectionListener {

    public abstract void OnConnectionReady(@NonNull NestedWorldSocketAPI nestedWorldSocketAPI);

    public abstract void OnConnectionLost();
}