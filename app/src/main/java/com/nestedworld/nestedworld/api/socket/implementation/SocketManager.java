package com.nestedworld.nestedworld.api.socket.implementation;

import android.support.annotation.NonNull;

import com.nestedworld.nestedworld.api.socket.listener.SocketListener;
import com.nestedworld.nestedworld.helper.log.LogHelper;

import org.msgpack.core.MessageInsufficientBufferException;
import org.msgpack.core.MessagePack;
import org.msgpack.core.MessagePacker;
import org.msgpack.core.MessageUnpacker;
import org.msgpack.value.MapValue;
import org.msgpack.value.ValueFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.LinkedList;

public class SocketManager implements Runnable {
    private final String TAG = getClass().getSimpleName();
    private String hostname;
    private int port;
    private int timeOut;
    private Socket socket;
    private Thread thread;/* Stores the thread used to listen for incoming data. */
    private LinkedList<SocketListener> listeners; /* Stores the list of SocketListeners to notify whenever an onEvent occurs. */
    private MessagePacker messagePacker;/*input stream reader.*/
    private MessageUnpacker messageUnpacker;/*output stream writer.*/

    /*
    ** Constructor
     */
    public SocketManager(@NonNull String hostname, int port) {
        this.hostname = hostname;
        this.port = port;

        this.socket = null;
        this.thread = new Thread(this);
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

    public synchronized void connect() {
        LogHelper.d(TAG, "Trying to connect...");

        if (socket != null) {
            LogHelper.d(TAG, "Already connected");
            return;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    /*Init socket*/
                    socket = new Socket();
                    socket.connect(new InetSocketAddress(hostname, port), timeOut);

                    /*Init serializer / deserializer */
                    messagePacker = new MessagePack.PackerConfig().newPacker(socket.getOutputStream());
                    messageUnpacker = new MessagePack.UnpackerConfig().newUnpacker(socket.getInputStream());

                    /*Display some log*/
                    LogHelper.d(TAG, "Connection Success");

                    /*Send notification*/
                    notifySocketConnected();

                    /*Init reading thread*/
                    thread.start();
                } catch (IOException e) {
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
        }).start();
    }

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
    *  Listens for messages while the socket is connected.
	 * use the connect() method before.
     */
    @Override
    public void run() {
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
        for (SocketListener listener : listeners) {
            listener.onSocketConnected(SocketListener.SocketEvent.SOCKET_CONNECTED);
        }
    }

    private void notifySocketDisconnected() {
        for (SocketListener listener : listeners) {
            listener.onSocketDisconnected(SocketListener.SocketEvent.SOCKET_DISCONNECTED);
        }
    }

    private void notifyMessageSent() {
        for (SocketListener listener : listeners) {
            listener.onMessageSent(SocketListener.SocketEvent.SOCKET_MESSAGE_SENT);
        }
    }

    private void notifyMessageReceived(@NonNull String message) {
        for (SocketListener listener : listeners) {
            listener.onMessageReceived(SocketListener.SocketEvent.SOCKET_MESSAGE_RECEIVED, message);
        }
    }
}

