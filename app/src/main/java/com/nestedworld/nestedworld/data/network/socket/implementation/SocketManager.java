package com.nestedworld.nestedworld.data.network.socket.implementation;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.nestedworld.nestedworld.data.network.socket.listener.SocketListener;
import com.nestedworld.nestedworld.helpers.log.LogHelper;

import org.msgpack.core.MessageInsufficientBufferException;
import org.msgpack.core.MessagePack;
import org.msgpack.core.MessagePacker;
import org.msgpack.core.MessageUnpacker;
import org.msgpack.value.ImmutableValue;
import org.msgpack.value.MapValue;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.LinkedList;

public final class SocketManager {
    private final String TAG = getClass().getSimpleName();

    private final String mHostname;
    private final int mPort;
    private final LinkedList<SocketListener> mListeners = new LinkedList<>(); /* Stores the list of SocketListeners to notify whenever an onEvent occurs. */
    private int mTimeOut;
    private boolean mIsConnected = false;
    @Nullable
    private Socket mSocket = new Socket();
    @Nullable
    private MessagePacker mMessagePacker;/*input stream reader.*/
    @Nullable
    private MessageUnpacker mMessageUnpacker;/*output stream writer.*/

    /*
     * #############################################################################################
     * # Constructor
     * #############################################################################################
     */
    /**
     * See {@link #SocketManager(String, int, int)}
     * @param hostname
     * @param port
     */
    public SocketManager(@NonNull final String hostname,
                         final int port) {
        this(hostname, port, 0);
    }

    /**
     * Create a new unconnectedSocket
     * @param hostname
     * @param port
     * @param timeOut
     */
    public SocketManager(@NonNull final String hostname,
                         final int port,
                         final int timeOut) {
        LogHelper.d(TAG, "init SocketManager: mHostname=" + hostname + " mPort=" + port + " mTimeOut=" + timeOut);

        mHostname = hostname;
        mPort = port;
        mTimeOut = timeOut;

        mMessagePacker = null;
        mMessageUnpacker = null;
    }

    /*
     * #############################################################################################
     * # Public method
     * #############################################################################################
     */
    public synchronized void setTimeOut(final int timeOut) {
        mTimeOut = timeOut;
    }

    public synchronized void addSocketListener(@NonNull final SocketListener socketListener) {
        mListeners.add(socketListener);
    }

    public synchronized void removeSocketListener(@NonNull final SocketListener socketListener) {
        mListeners.remove(socketListener);
    }

    /**
     * Connects the mSocket to the given remote host address and mPort specified in the constructor
     * connecting method will block until the connection is established or an error occurred.
     */
    public synchronized void connect() {
        LogHelper.d(TAG, "Trying to connect...");

        try {
            /*Init mSocket*/
            if (mSocket == null) {
                throw new IllegalArgumentException("Can't connect if mSocket is null");
            }
            mSocket.connect(new InetSocketAddress(mHostname, mPort), mTimeOut);
            mIsConnected = true;

            /*Init serializer / deserializer */
            mMessagePacker = new MessagePack.PackerConfig().newPacker(mSocket.getOutputStream());
            mMessageUnpacker = new MessagePack.UnpackerConfig().newUnpacker(mSocket.getInputStream());

            /*Display some log*/
            LogHelper.d(TAG, "Connection Success");

            /*Send notification*/
            notifySocketConnected();

            /*Init a listeningThread*/
            startListeningTask();
        } catch (IOException | IllegalArgumentException e) {
            mIsConnected = false;
            mSocket = null;
            mMessagePacker = null;
            mMessageUnpacker = null;

            /*Display some log*/
            LogHelper.e(TAG, "Connection failed");

            e.printStackTrace();

            /*Send notification*/
            notifySocketDisconnected();
        }
    }

    /**
     * Closes the mSocket.
     * It is not possible to reconnect or rebind to the mSocket
     * which means a new mSocket instance has to be created.
     */
    public synchronized void disconnect() {
        try {
            mIsConnected = false;

            if (mSocket != null) {
                mSocket.close();
                mSocket = null;
            }
            if (mMessagePacker != null) {
                mMessagePacker.close();
                mMessagePacker = null;
            }
            if (mMessageUnpacker != null) {
                mMessageUnpacker.close();
                mMessageUnpacker = null;
            }
            mListeners.clear();

        } catch (IOException e) {
            e.printStackTrace();
        }

        notifySocketDisconnected();
    }

    /**
     * Runnable implementation
     * Listens for messages while the mSocket is connected.
     * use the connect() method before.
     */
    public synchronized void send(@NonNull final MapValue message) {
        LogHelper.d(TAG, "Sending: " + message);
        startSendingTask(message);
    }

    /*
     * #############################################################################################
     * # Internal method
     * #############################################################################################
     */
    private void startSendingTask(@NonNull final MapValue message) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (mMessagePacker != null) {
                        mMessagePacker.packValue(message);
                        mMessagePacker.flush();
                    }
                } catch (IOException e) {
                    LogHelper.d(TAG, "Can't send message");
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void startListeningTask() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (mSocket == null || !mSocket.isConnected()) {
                        throw new UnsupportedOperationException("You should call connect() before");
                    }

                    /*Send notification*/
                    notifySocketListening();

                    LogHelper.d(TAG, "Listening on mSocket...");
                    while (mIsConnected) {
                        if (mMessageUnpacker != null) {
                            ImmutableValue message = mMessageUnpacker.unpackValue();
                            notifyMessageReceived(message);
                        }
                    }

                } catch (IOException | MessageInsufficientBufferException | UnsupportedOperationException e) {
                    LogHelper.d(TAG, "Connection close by server");
                    notifySocketDisconnected();
                }
            }
        }).start();
    }

    private void notifySocketConnected() {
        LogHelper.d(TAG, "notifySocketConnected");

        for (final SocketListener listener : mListeners) {
            listener.onSocketConnected();
        }
    }

    private void notifySocketListening() {
        LogHelper.d(TAG, "notifySocketListening");

        for (final SocketListener listener : mListeners) {
            listener.onSocketListening();
        }
    }

    private void notifySocketDisconnected() {
        LogHelper.d(TAG, "notifySocketDisconnected");

        for (final SocketListener listener : mListeners) {
            listener.onSocketDisconnected();
        }
    }

    private void notifyMessageReceived(@NonNull final ImmutableValue message) {
        LogHelper.d(TAG, "Message receive: " + message.toString());

        for (final SocketListener listener : mListeners) {
            listener.onMessageReceived(message);
        }
    }
}