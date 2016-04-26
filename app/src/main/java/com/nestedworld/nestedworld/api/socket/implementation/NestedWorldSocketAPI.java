package com.nestedworld.nestedworld.api.socket.implementation;

import android.support.annotation.NonNull;

import com.nestedworld.nestedworld.api.socket.listener.ConnectionListener;
import com.nestedworld.nestedworld.api.socket.listener.SocketListener;
import com.nestedworld.nestedworld.helper.log.LogHelper;

public class NestedWorldSocketAPI {

    private final String TAG = getClass().getSimpleName();
    private static NestedWorldSocketAPI mSingleton;
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
            public void onSocketConnected(@NonNull SocketEvent socketEvent) {
                LogHelper.d(TAG, "Successfully Fully got a connection");
                mSingleton = NestedWorldSocketAPI.this;
                connectionListener.OnConnectionReady(mSingleton);
            }

            @Override
            public void onSocketDisconnected(@NonNull SocketEvent socketEvent) {
                LogHelper.e(TAG, "Connection failed");
                reset();
                connectionListener.OnConnectionLost();
            }

            @Override
            public void onMessageSent(@NonNull SocketEvent socketEven) {

            }

            @Override
            public void onMessageReceived(@NonNull SocketEvent socketEvent, String message) {

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
    public void sendAttack(@NonNull String string) {
        mSocketManager.send(string);
    }
}

