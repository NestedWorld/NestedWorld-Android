package com.nestedworld.nestedworld.network.socket.implementation;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;

import com.nestedworld.nestedworld.helpers.log.LogHelper;
import com.nestedworld.nestedworld.network.socket.listener.SocketListener;

import org.msgpack.core.MessageInsufficientBufferException;
import org.msgpack.core.MessagePack;
import org.msgpack.core.MessagePacker;
import org.msgpack.core.MessageUnpacker;
import org.msgpack.value.MapValue;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.LinkedList;

public class SocketManager implements Runnable {
    private final String TAG = getClass().getSimpleName();
    private final String hostname;
    private final int port;
    private final Thread listeningThread;/* Stores the Thread used to listen for incoming data. */
    private final LinkedList<SocketListener> listeners; /* Stores the list of SocketListeners to notify whenever an onEvent occurs. */
    private int timeOut;
    private Socket socket;
    private MessagePacker messagePacker;/*input stream reader.*/
    private MessageUnpacker messageUnpacker;/*output stream writer.*/

    /*
    ** Constructor (Creates a new unconnected socket).
     */
    public SocketManager(@NonNull String hostname, int port) {
        this(hostname, port, 0);
    }

    public SocketManager(@NonNull String hostname, int port, int timeOut) {
        this.hostname = hostname;
        this.port = port;
        this.timeOut = timeOut;

        this.socket = new Socket();
        this.listeningThread = new Thread(this);
        this.listeners = new LinkedList<>();

        this.messagePacker = null;
        this.messageUnpacker = null;
    }

    /*
    ** Setter
     */
    public void setTimeOut(int timeOut) {
        this.timeOut = timeOut;
    }

    /*
    ** Public method
     */
    public synchronized void addSocketListener(@NonNull SocketListener socketListener) {
        listeners.add(socketListener);
    }

    public synchronized void removeSocketListener(@NonNull SocketListener socketListener) {
        listeners.remove(socketListener);
    }

    //Connects the socket to the given remote host address and port specified in the constructor
    //connecting method will block until the connection is established or an error occurred.
    public synchronized void connect() {
        LogHelper.d(TAG, "Trying to connect...");

        try {
            /*Init socket*/
            socket.connect(new InetSocketAddress(hostname, port), timeOut);

            /*Init serializer / deserializer */
            messagePacker = new MessagePack.PackerConfig().newPacker(socket.getOutputStream());
            messageUnpacker = new MessagePack.UnpackerConfig().newUnpacker(socket.getInputStream());

            /*Display some log*/
            LogHelper.d(TAG, "Connection Success");

            /*Send notification*/
            notifySocketConnected();

            /*Init reading listeningThread*/
            listeningThread.start();
        } catch (IOException | IllegalArgumentException e) {
            socket = null;
            messagePacker = null;
            messageUnpacker = null;

            /*Display some log*/
            LogHelper.e(TAG, "Connection failed");

            e.printStackTrace();

            /*Send notification*/
            notifySocketDisconnected();
        }
    }

    // Closes the socket.
    // It is not possible to reconnect or rebind to the socket
    // which means a new socket instance has to be created.
    public synchronized void disconnect() {
        if (socket == null) {
            return;
        }
        try {
            socket.close();
            messagePacker.close();
            messageUnpacker.close();
            LogHelper.d(TAG, "Socked closed");
        } catch (IOException e) {
            LogHelper.e(TAG, "Can't close socket");
        }
        socket = null;
        messagePacker = null;
        messageUnpacker = null;
        notifySocketDisconnected();
    }

    /*
    ** Runnable implementation
     * Listens for messages while the socket is connected.
	 * use the connect() method before.
     */
    @Override
    public void run() {
        if (socket == null || !socket.isConnected()) {
            throw new UnsupportedOperationException("You should call connect() before");
        }

        try {
            LogHelper.d(TAG, "Listening on socket...");
            while (true) {
                String message = messageUnpacker.unpackValue().toString();
                notifyMessageReceived(message);
                LogHelper.d(TAG, "receive message:" + message);
            }

        } catch (IOException | MessageInsufficientBufferException e) {
            LogHelper.d(TAG, "Connection close by server");
            notifySocketDisconnected();
        }
    }

    public synchronized void send(@NonNull MapValue message) {
        LogHelper.d(TAG, "Sending: " + message);

        try {
            messagePacker.packValue(message);
            messagePacker.flush();
        } catch (IOException e) {
            LogHelper.d(TAG, "Can't send message");
            e.printStackTrace();
        }
        notifyMessageSent();
    }

    /*
    ** Utils
     */
    private void notifySocketConnected() {
        for (final SocketListener listener : listeners) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    listener.onSocketConnected();
                }
            });
        }
    }

    private void notifySocketDisconnected() {
        for (final SocketListener listener : listeners) {
            listener.onSocketDisconnected();
        }
    }

    private void notifyMessageSent() {
        for (final SocketListener listener : listeners) {
            listener.onMessageSent();
        }
    }

    private void notifyMessageReceived(@NonNull final String message) {
        for (final SocketListener listener : listeners) {
            listener.onMessageReceived(message);
        }
    }
}
