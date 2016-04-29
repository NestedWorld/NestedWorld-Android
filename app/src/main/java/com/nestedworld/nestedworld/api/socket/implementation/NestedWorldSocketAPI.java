package com.nestedworld.nestedworld.api.socket.implementation;

import android.content.Context;
import android.support.annotation.NonNull;

import com.nestedworld.nestedworld.api.socket.listener.ConnectionListener;
import com.nestedworld.nestedworld.api.socket.listener.SocketListener;
import com.nestedworld.nestedworld.api.socket.models.DefaultModel;
import com.nestedworld.nestedworld.authenticator.UserManager;
import com.nestedworld.nestedworld.helper.log.LogHelper;

import org.msgpack.value.ValueFactory;

public class NestedWorldSocketAPI {

    private static NestedWorldSocketAPI mSingleton;
    private final String TAG = getClass().getSimpleName();
    private final SocketManager mSocketManager;

    /*
    ** Constructor
     */
    private NestedWorldSocketAPI(@NonNull final ConnectionListener connectionListener) {
        mSocketManager = new SocketManager("eip.kokakiwi.net", 6464);
        mSocketManager.setTimeOut(10000);
        LogHelper.d(TAG, "Waiting for a new connection...");
        mSocketManager.addSocketListener(new SocketListener() {
            @Override
            public void onSocketConnected() {
                LogHelper.d(TAG, "Successfully Fully got a connection");
                mSingleton = NestedWorldSocketAPI.this;
                connectionListener.OnConnectionReady(mSingleton);
            }

            @Override
            public void onSocketDisconnected() {
                LogHelper.e(TAG, "Connection failed");
                reset();
                connectionListener.OnConnectionLost();
            }

            @Override
            public void onMessageSent() {

            }

            @Override
            public void onMessageReceived(String message) {
                //TODO parse content and call listener
            }
        });
        mSocketManager.connect();
    }

    /*
    ** Singleton
     */
    public static void getInstance(@NonNull ConnectionListener connectionListener) {
        if (mSingleton == null) {
            new NestedWorldSocketAPI(connectionListener);
        } else {
            connectionListener.OnConnectionReady(mSingleton);
        }
    }

    /*
    ** Avoid leek when log out
     */
    public void reset() {
        mSingleton = null;
        mSocketManager.disconnect();
    }

    /*
    ** Public method
     */
    private void addAuthStateToMapValue(@NonNull Context context, @NonNull ValueFactory.MapBuilder mapBuilder) {
        String token = UserManager.get(context).getCurrentAuthToken(context);
        mapBuilder.put(ValueFactory.newString("token"), ValueFactory.newString(token == null ? "" : token));
    }

    public void combatRequest(@NonNull Context context, @NonNull DefaultModel data) {
        ValueFactory.MapBuilder mapBuilder = data.serialise();
        addAuthStateToMapValue(context, mapBuilder);
        mSocketManager.send(mapBuilder.build());
    }

    public void chatRequest(@NonNull Context context, @NonNull DefaultModel data) {
        ValueFactory.MapBuilder mapBuilder = data.serialise();
        addAuthStateToMapValue(context, mapBuilder);
        mSocketManager.send(mapBuilder.build());
    }
}

