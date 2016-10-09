package com.nestedworld.nestedworld.network.socket.implementation;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.nestedworld.nestedworld.database.models.Session;
import com.nestedworld.nestedworld.helpers.log.LogHelper;
import com.nestedworld.nestedworld.helpers.session.SessionHelper;
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
    ** Utils
     */
    @Nullable
    private static SocketMessageType.MessageKind getMessageKind(@NonNull final Map<Value, Value> message) {
        if (message.containsKey(ValueFactory.newString("type"))) {
            final String type = message.get(ValueFactory.newString("type")).asStringValue().asString();

            //check if we know the type
            if (SocketMessageType.messageType.containsValue(type)) {
                return SocketMessageType.messageType.getKeyFromValue(type);
            }
        }
        return null;
    }

    @Nullable
    private static SocketMessageType.MessageKind getMessageIdKind(@NonNull final Map<Value, Value> message) {
        if (message.containsKey(ValueFactory.newString("id"))) {
            //get the messageId
            String messageId = message.get(ValueFactory.newString("id")).asStringValue().asString();
            return SocketMessageType.messageType.getKeyFromValue(messageId);
        }
        return null;
    }

    /*
    ** Public method
     */
    public void addListener(@NonNull final ConnectionListener connectionListener) {
        LogHelper.d(TAG, "addListener");

        //Add listener to the list of listener
        mConnectionListener.add(connectionListener);

        //If we have an authenticated connection, we directly call onConnectionReady()
        if (isAuth) {
            LogHelper.d(TAG, "addListener > calling onConnectionReady");
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
        Session session = SessionHelper.getSession();
        if (session != null) {
            LogHelper.d(TAG, "authRequest > sending");

            AuthRequest authRequest = new AuthRequest(session.authToken);
            sendMessage(authRequest.serialise(),
                    SocketMessageType.MessageKind.TYPE_AUTHENTICATE,
                    SocketMessageType.messageType.getValueFromKey(SocketMessageType.MessageKind.TYPE_AUTHENTICATE));
        } else {
            LogHelper.d(TAG, "authRequest > session null");
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
        SocketMessageType.MessageKind kind = getMessageKind(message);
        SocketMessageType.MessageKind idKind = getMessageIdKind(message);

        LogHelper.d(TAG, "kind=" + kind);
        LogHelper.d(TAG, "idKind=" + idKind);

        if (kind != null) {
            //If we're not auth, we check if it's our auth response
            if (!isAuth) {
                if (idKind == SocketMessageType.MessageKind.TYPE_AUTHENTICATE) {
                    parseAuthMessage(message);
                    return;
                }
            } else {
                notifyMessageReceive(message, kind, idKind);
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
        LogHelper.d(TAG, "onSocketConnected");

        //Connection success, we can init the singleton
        mSingleton = NestedWorldSocketAPI.this;

        //!\ Do not send request here see onSocketListening) /!\
    }

    @Override
    public void onSocketDisconnected() {
        LogHelper.d(TAG, "onSocketDisconnected");

        mSingleton = null;
        notifySocketDisconnected();
    }

    @Override
    public void onSocketListening() {
        LogHelper.d(TAG, "onSocketListening");

        //The socket is listening, we can send our request

        //Auth the connection
        authRequest();
    }

    @Override
    public void onMessageReceived(@NonNull final ImmutableValue message) {
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

    private void notifyMessageReceive(@NonNull final Map<Value, Value> message, @NonNull final SocketMessageType.MessageKind messageKind, @Nullable final SocketMessageType.MessageKind idKind) {
        LogHelper.d(TAG, "Notify: " + SocketMessageType.messageType.getValueFromKey(messageKind));

        //Call connectionListener.onMessageReceived() inside the main thread
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                for (ConnectionListener connectionListener : mConnectionListener) {
                    if (connectionListener != null) {
                        connectionListener.onMessageReceived(message, messageKind, idKind);
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

