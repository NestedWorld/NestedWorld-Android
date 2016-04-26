package com.nestedworld.nestedworld.api.socket;

import android.support.annotation.NonNull;

import com.nestedworld.nestedworld.api.socket.callback.Callback;
import com.nestedworld.nestedworld.api.socket.runnable.PacketSender;
import com.nestedworld.nestedworld.helper.log.LogHelper;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class NestedWorldSocketAPI {
    private final static String BASE_URL = "127.0.0.1";
    private final static int PORT = 2009;
    private static NestedWorldSocketAPI mSingleton;
    private final String TAG = getClass().getSimpleName();
    private Socket mSocket;

    /*
    ** Constructor
     */
    private NestedWorldSocketAPI(@NonNull final Callback callback) {
        if (mSingleton != null) {
            return;
        }

        LogHelper.d(TAG, "Init API(socket) (end_point = " + BASE_URL + ":" + PORT + ")");

        /*Init socket from another thread (avoid networkOnMainThreadException*/
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    mSocket = new Socket("10.0.2.2", 2009);
                    callback.onConnexionReady(mSingleton);
                } catch (IOException e) {
                    LogHelper.e(TAG, "Connexion failed");
                    reset();
                    callback.onConnexionFailed();
                }
            }
        }).start();
    }

    /*
    ** Singleton
     */
    public static void getInstance(@NonNull final Callback callback) {
        if (mSingleton == null) {
            mSingleton = new NestedWorldSocketAPI(callback);
        }
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
    public void sendMessage(@NonNull final String message) {
        if (mSocket == null) {
            LogHelper.d(TAG, "sendMessage: mSocket = null");
            return;
        }

        try {
            PrintWriter out = new PrintWriter(mSocket.getOutputStream());
            new Thread(new PacketSender(out, message)).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

