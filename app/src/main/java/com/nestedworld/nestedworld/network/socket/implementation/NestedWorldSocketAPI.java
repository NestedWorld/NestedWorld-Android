package com.nestedworld.nestedworld.network.socket.implementation;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;

import com.nestedworld.nestedworld.network.socket.listener.ConnectionListener;
import com.nestedworld.nestedworld.network.socket.listener.SocketListener;
import com.nestedworld.nestedworld.network.socket.models.DefaultModel;
import com.nestedworld.nestedworld.helpers.user.UserManager;
import com.nestedworld.nestedworld.helpers.log.LogHelper;

import org.msgpack.value.ValueFactory;

public final class NestedWorldSocketAPI implements SocketListener {

    //Singleton
    private static NestedWorldSocketAPI mSingleton;

    //Static field
    private final static int TIME_OUT = 10000;
    private final static String HOST = "eip.kokakiwi.net";
    private final static int PORT = 6464;

    //Private field
    private final String TAG = getClass().getSimpleName();
    private final SocketManager mSocketManager;
    private final ConnectionListener mConnectionListener;

    /*
    ** Constructor
     */
    private NestedWorldSocketAPI(@NonNull final ConnectionListener connectionListener) {
        //Init private field
        mConnectionListener = connectionListener;

        //Init the socket
        mSocketManager = new SocketManager(HOST, PORT);
        mSocketManager.setTimeOut(TIME_OUT);
        mSocketManager.addSocketListener(this);

        LogHelper.d(TAG, "Waiting for connection...");

        //Connect() require networking so we call it inside a thread
        new Thread(new Runnable() {
            @Override
            public void run() {
                mSocketManager.connect();
            }
        }).start();
    }

    /*
    ** Singleton
     */
    public static void getInstance(@NonNull ConnectionListener connectionListener) {
        if (mSingleton == null) {
            new NestedWorldSocketAPI(connectionListener);
        } else {
            connectionListener.onConnectionReady(mSingleton);
        }
    }

    /*
    ** Avoid leek when log out
     */
    public static void reset() {
        mSingleton = null;
    }

    /*
    ** SocketListener implementation
     */
    @Override
    public void onSocketConnected() {
        LogHelper.d(TAG, "Successfully got a connection");

        //Connection success, we can init the singleton
        mSingleton = NestedWorldSocketAPI.this;

        //Call the listener inside the main thread
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                mConnectionListener.onConnectionReady(mSingleton);
            }
        });
    }

    @Override
    public void onSocketDisconnected() {
        LogHelper.e(TAG, "Connection failed");
        mSingleton = null;

        //Call the listener inside the main thread
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                mConnectionListener.onConnectionLost();
            }
        });
    }

    @Override
    public void onMessageSent() {
        LogHelper.d(TAG, "Message sent");
        //A message has been send, should call some listener
    }

    @Override
    public void onMessageReceived(String message) {
        LogHelper.d(TAG, "Message received");
        //TODO parse content and call listener
    }

    /*
    ** Private method
     */
    private void addAuthStateToMapValue(@NonNull Context context, @NonNull ValueFactory.MapBuilder mapBuilder) {
        String token = UserManager.get().getCurrentAuthToken(context);
        mapBuilder.put(ValueFactory.newString("token"), ValueFactory.newString(token));
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

