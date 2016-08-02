package com.nestedworld.nestedworld.network.socket.implementation;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;

import com.nestedworld.nestedworld.helpers.log.LogHelper;
import com.nestedworld.nestedworld.helpers.session.SessionManager;
import com.nestedworld.nestedworld.models.Session;
import com.nestedworld.nestedworld.network.socket.listener.ConnectionListener;
import com.nestedworld.nestedworld.network.socket.listener.SocketListener;
import com.nestedworld.nestedworld.network.socket.models.request.DefaultRequest;
import com.nestedworld.nestedworld.network.socket.models.request.auth.AuthRequest;

import org.msgpack.value.ImmutableValue;
import org.msgpack.value.Value;
import org.msgpack.value.ValueFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class NestedWorldSocketAPI implements SocketListener {

    //Static field
    private final static int TIME_OUT = 10000;
    private final static String HOST = "eip.kokakiwi.net";
    private final static int PORT = 6464;
    //Singleton
    private static NestedWorldSocketAPI mSingleton;
    //Private field
    private final String TAG = getClass().getSimpleName();
    private final SocketManager mSocketManager;
    private final List<ConnectionListener> mConnectionListener = new ArrayList<>();
    private boolean isAuth = false;

    /*
    ** Constructor
     */
    private NestedWorldSocketAPI() {
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
    public static NestedWorldSocketAPI getInstance() {
        if (mSingleton == null) {
            mSingleton = new NestedWorldSocketAPI();
        }
        return mSingleton;
    }

    /*
    ** Avoid leek when log out
     */
    public static void reset() {
        mSingleton = null;
    }

    /*
    ** Public method
     */
    public void addListener(@NonNull final ConnectionListener connectionListener) {
        //Add listener to the list of listener
        mConnectionListener.add(connectionListener);

        //If we have an authenticated connection, we directly call onConnectionReady()
        if (isAuth) {
            connectionListener.onConnectionReady(mSingleton);
        }
    }

    public void removeListener(@NonNull final ConnectionListener connectionListener) {
        mConnectionListener.remove(connectionListener);
    }

    public void sendRequest(@NonNull final DefaultRequest data, @NonNull final SocketMessageType.MessageKind messageKind) {
        //Send a message with generic id (from messageType)
        sendRequest(data, messageKind, SocketMessageType.messageType.getValueFromKey(messageKind));
    }

    public void sendRequest(@NonNull final DefaultRequest data, @NonNull final SocketMessageType.MessageKind messageKind, @NonNull final String requestId) {
        //Send a message with the given id
        sendMessage(data.serialise(), messageKind, requestId);
    }

    /*
    ** Private method
     */
    private void authRequest() {
        Session session = SessionManager.get().getSession();
        if (session != null) {
            AuthRequest authRequest = new AuthRequest(session.authToken);
            sendMessage(authRequest.serialise(),
                    SocketMessageType.MessageKind.TYPE_AUTHENTICATE,
                    SocketMessageType.messageType.getValueFromKey(SocketMessageType.MessageKind.TYPE_AUTHENTICATE));
        }
    }

    private void sendMessage(@NonNull final ValueFactory.MapBuilder mapBuilder, @NonNull final SocketMessageType.MessageKind messageKind, @NonNull final String requestId) {
        //Add id field
        mapBuilder.put(ValueFactory.newString("id"), ValueFactory.newString(requestId));

        //Add type field
        mapBuilder.put(ValueFactory.newString("type"), ValueFactory.newString(SocketMessageType.messageType.getValueFromKey(messageKind)));

        //Send message
        mSocketManager.send(mapBuilder.build());
    }

    private void parseSocketMessage(@NonNull final Map<Value, Value> message) {
        //Check if the message is a response
        if (message.containsKey(ValueFactory.newString("id"))) {
            //get the messageId
            final String messageId = message.get(ValueFactory.newString("id")).asStringValue().asString();

            //check if we know this id
            if (SocketMessageType.messageType.containsValue(messageId)) {
                final SocketMessageType.MessageKind kind = SocketMessageType.messageType.getKeyFromValue(messageId);
                //Check it it's an auth response
                if (kind == SocketMessageType.MessageKind.TYPE_AUTHENTICATE) {
                    parseAuthMessage(message);
                    return;
                } else {
                    notifyMessageReceive(kind, message);
                    return;
                }
            }
        }

        //It's a spontaneous message, try to found the type
        if (message.containsKey(ValueFactory.newString("type"))) {
            final String type = message.get(ValueFactory.newString("type")).asStringValue().asString();

            //check if we know the type
            if (SocketMessageType.messageType.containsValue(type)) {
                final SocketMessageType.MessageKind kind = SocketMessageType.messageType.getKeyFromValue(type);
                notifyMessageReceive(kind, message);
                return;
            }
        }

        //Display some log
        LogHelper.d(TAG, "Can't parse message: " + message);
    }

    private void parseAuthMessage(@NonNull final Map<Value, Value> message) {
        if (message.get(ValueFactory.newString("result")).asStringValue().asString().equals("success")) {
            isAuth = true;
            notifySocketReady();
        } else {
            onSocketDisconnected();
            mSocketManager.disconnect();
        }
    }

    /*
    ** SocketListener implementation
     */
    @Override
    public void onSocketConnected() {
        LogHelper.d(TAG, "Successfully got a connection");

        //Connection success, we can init the singleton
        mSingleton = NestedWorldSocketAPI.this;

        //Auth the connection
        authRequest();
    }

    @Override
    public void onSocketDisconnected() {
        mSingleton = null;
        notifySocketDisconnected();
    }

    @Override
    public void onMessageReceived(ImmutableValue message) {
        switch (message.getValueType()) {
            case MAP:
                final Map<Value, Value> map = message.asMapValue().map();
                parseSocketMessage(map);
                break;
            default:
                //Cannot parse the message, undefined in the protocol
                LogHelper.d(TAG, "Can't parse message");
                break;
        }
    }

    /*
    ** Notification (will call listener on the main thread)
     */
    private void notifySocketDisconnected() {
        //Call connectionListener.onConnectionLost() inside the main thread
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                for (ConnectionListener connectionListener : mConnectionListener) {
                    if (connectionListener != null) {
                        connectionListener.onConnectionLost();
                    }
                }
            }
        });
    }

    private void notifyMessageReceive(@NonNull final SocketMessageType.MessageKind messageKind, @NonNull final Map<Value, Value> message) {
        LogHelper.d(TAG, "Notify: " + SocketMessageType.messageType.getValueFromKey(messageKind));

        //Call connectionListener.onMessageReceived() inside the main thread
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                for (ConnectionListener connectionListener : mConnectionListener) {
                    if (connectionListener != null) {
                        connectionListener.onMessageReceived(messageKind, message);
                    }
                }
            }
        });
    }

    private void notifySocketReady() {
        //Call connectionListener.onConnectionReady() inside the main thread
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                for (ConnectionListener connectionListener : mConnectionListener) {
                    if (connectionListener != null) {
                        connectionListener.onConnectionReady(mSingleton);
                    }
                }
            }
        });
    }

}

