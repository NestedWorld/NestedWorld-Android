package com.nestedworld.nestedworld.data.network.socket.implementation;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.nestedworld.nestedworld.data.database.entities.session.Session;
import com.nestedworld.nestedworld.data.network.socket.listener.ConnectionListener;
import com.nestedworld.nestedworld.data.network.socket.listener.SocketListener;
import com.nestedworld.nestedworld.data.network.socket.models.request.DefaultRequest;
import com.nestedworld.nestedworld.data.network.socket.models.request.auth.AuthRequest;
import com.nestedworld.nestedworld.helpers.log.LogHelper;
import com.nestedworld.nestedworld.helpers.session.SessionHelper;

import org.msgpack.value.ImmutableValue;
import org.msgpack.value.Value;
import org.msgpack.value.ValueFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class NestedWorldSocketAPI implements SocketListener {

    private final static int TIME_OUT = 10000;
    private static NestedWorldSocketAPI mSingleton;
    private final String TAG = getClass().getSimpleName();
    @Nullable
    private final SocketManager mSocketManager;
    private final List<ConnectionListener> mConnectionListener = new ArrayList<>();
    private boolean isAuth = false;

    /*
     * #############################################################################################
     * # Constructor
     * #############################################################################################
     */
    private NestedWorldSocketAPI() {
        //Init the socket
        mSocketManager = new SocketManager(SocketEndPoint.SOCKET_END_POINT, SocketEndPoint.SOCKET_PORT);
        mSocketManager.setTimeOut(TIME_OUT);
        mSocketManager.addSocketListener(this);

        LogHelper.d(TAG, "Waiting for connection...");

        //Connect() require networking so we call it inside a thread
        new Thread(new Runnable() {
            @Override
            public void run() {
                //Check if singleton hasn't been reset before thread launching
                mSocketManager.connect();
            }
        }).start();
    }

    /*
     * #############################################################################################
     * # Public (static) method
     * #############################################################################################
     */

    /**
     * Singleton
     *
     * @return singleton instance
     */
    @NonNull
    public static NestedWorldSocketAPI getInstance() {
        if (mSingleton == null) {
            mSingleton = new NestedWorldSocketAPI();
        }
        return mSingleton;
    }

    /**
     * Avoid singleton leak
     */
    public static void reset() {
        mSingleton = null;
    }

    /*
     * #############################################################################################
     * # Public method
     * #############################################################################################
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

    public void sendRequest(@NonNull final DefaultRequest data,
                            @NonNull final SocketMessageType.MessageKind messageKind) {
        //Send a message with generic id (from MESSAGE_TYPE)
        sendRequest(data, messageKind, SocketMessageType.MESSAGE_TYPE.getValueFromKey(messageKind));
    }

    public void sendRequest(@NonNull final DefaultRequest data,
                            @NonNull final SocketMessageType.MessageKind messageKind,
                            @NonNull final String requestId) {
        //Send a message with the given id
        sendMessage(data.serialise(), messageKind, requestId);
    }

    public void disconnect() {
        if (mSocketManager != null) {
            mSocketManager.disconnect();
        }
        mSingleton = null;
    }

    /*
     * #############################################################################################
     * # SocketListener implementation
     * #############################################################################################
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

        if (mSocketManager != null) {
            mSocketManager.disconnect();
        }
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
     * #############################################################################################
     * # Internal method
     * #############################################################################################
     */
    private void authRequest() {
        final Session session = SessionHelper.getSession();
        if (session != null) {
            LogHelper.d(TAG, "authRequest > sending");

            final AuthRequest authRequest = new AuthRequest(session.authToken);
            final String requestId = SocketMessageType.MESSAGE_TYPE.getValueFromKey(SocketMessageType.MessageKind.TYPE_AUTHENTICATE);

            if (requestId == null) {
                throw new IllegalArgumentException("No key associated with SocketMessageType.MessageKind.TYPE_AUTHENTICATE");
            }

            sendMessage(authRequest.serialise(),
                    SocketMessageType.MessageKind.TYPE_AUTHENTICATE,
                    requestId);
        } else {
            LogHelper.d(TAG, "authRequest > session null");
        }
    }

    private void sendMessage(@NonNull final ValueFactory.MapBuilder mapBuilder,
                             @NonNull final SocketMessageType.MessageKind messageKind,
                             @NonNull final String requestId) {
        if (mSocketManager != null) {
            //Add id field
            mapBuilder.put(ValueFactory.newString("id"), ValueFactory.newString(requestId));

            //Add type field
            mapBuilder.put(
                    ValueFactory.newString("type"),
                    ValueFactory.newString(SocketMessageType.MESSAGE_TYPE.getValueFromKey(messageKind)));

            //Send message
            mSocketManager.send(mapBuilder.build());
        }
    }

    private void parseSocketMessage(@NonNull final Map<Value, Value> message) {
        final SocketMessageType.MessageKind kind = getMessageKind(message);
        final SocketMessageType.MessageKind idKind = getMessageIdKind(message);

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
        }
    }

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

    private void notifyMessageReceive(@NonNull final Map<Value, Value> message,
                                      @NonNull final SocketMessageType.MessageKind messageKind,
                                      @Nullable final SocketMessageType.MessageKind idKind) {
        LogHelper.d(TAG, "Notify: " + SocketMessageType.MESSAGE_TYPE.getValueFromKey(messageKind));

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

    /*
     * #############################################################################################
     * # Internal (static) method
     * #############################################################################################
     */
    @Nullable
    private static SocketMessageType.MessageKind getMessageKind(@NonNull final Map<Value, Value> message) {
        if (message.containsKey(ValueFactory.newString("type"))) {
            final String type = message.get(ValueFactory.newString("type")).asStringValue().asString();

            //check if we know the type
            if (SocketMessageType.MESSAGE_TYPE.containsValue(type)) {
                return SocketMessageType.MESSAGE_TYPE.getKeyFromValue(type);
            }
        }
        return null;
    }

    @Nullable
    private static SocketMessageType.MessageKind getMessageIdKind(@NonNull final Map<Value, Value> message) {
        if (message.containsKey(ValueFactory.newString("id"))) {
            //get the messageId
            final String messageId = message.get(ValueFactory.newString("id")).asStringValue().asString();
            return SocketMessageType.MESSAGE_TYPE.getKeyFromValue(messageId);
        }
        return null;
    }
}

