package com.nestedworld.nestedworld.network.socket.implementation;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.util.Log;

import com.nestedworld.nestedworld.helpers.log.LogHelper;
import com.nestedworld.nestedworld.helpers.session.SessionManager;
import com.nestedworld.nestedworld.models.Session;
import com.nestedworld.nestedworld.network.socket.listener.ConnectionListener;
import com.nestedworld.nestedworld.network.socket.listener.SocketListener;
import com.nestedworld.nestedworld.network.socket.models.DefaultModel;

import org.msgpack.value.ImmutableMapValue;
import org.msgpack.value.ImmutableValue;
import org.msgpack.value.Value;
import org.msgpack.value.ValueFactory;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

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
    ** Public method (API)
     */
    public void authRequest(@NonNull String requestId) {
        Session session = SessionManager.get().getSession();
        if (session != null) {
            String token = session.authToken;

            ValueFactory.MapBuilder mapBuilder = ValueFactory.newMapBuilder();
            mapBuilder.put(ValueFactory.newString("type"), ValueFactory.newString("authenticate"));
            mapBuilder.put(ValueFactory.newString("token"), ValueFactory.newString(token));

            sendMessage(mapBuilder, requestId);
        }
    }

    public void combatRequest(@NonNull final DefaultModel data) {
        sendMessage(data.serialise(), "COMBAT_REQUEST");
    }

    /*
    ** Private method
     */
    private void sendMessage(@NonNull final ValueFactory.MapBuilder mapBuilder, @NonNull final String requestId) {
        mapBuilder.put(ValueFactory.newString("id"), ValueFactory.newString(requestId));
        mSocketManager.send(mapBuilder.build());
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
        //A message has been send, should call some listener
    }

    @Override
    public void onMessageReceived(ImmutableValue message) {
        LogHelper.d(TAG, "Message received");

        switch (message.getValueType()) {
            case MAP:
                final Map<Value, Value> map = message.asMapValue().map();
                final String messageId = map.get(ValueFactory.newString("id")).asStringValue().asString();

                //Call the listener inside the main thread
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        mConnectionListener.onMessageReceived(messageId, map);
                    }
                });


                break;
            default:
                //Cannot parse the message, undefined in the protocol
                break;
        }

    }
}

