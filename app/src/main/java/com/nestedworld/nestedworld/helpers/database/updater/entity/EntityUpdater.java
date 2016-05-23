package com.nestedworld.nestedworld.helpers.database.updater.entity;

import android.content.Context;
import android.support.annotation.NonNull;

public abstract class EntityUpdater extends Thread {
    private final static String TAG = EntityUpdater.class.getSimpleName();
    private final Context mContext;
    private final onEntityUpdated mCallback;

    public interface onEntityUpdated {
        void onSuccess();
        void onError();
    }

    /*
    ** Constructor
     */
    public EntityUpdater(@NonNull final Context context, @NonNull final onEntityUpdated callback) {
        mContext = context;
        mCallback = callback;
    }

    /*
    ** Method every child will have to implement
     */
    public abstract void update();

    /*
    ** Thread implementation
     */
    public void run() {
        try {
            update();
        } catch (Throwable t) {
            this.interrupt();
            //Thread has been stop
            onFinish(false);
        }
    }

    /*
    ** Utils
     */
    public Context getContext() {
        return mContext;
    }

    public void onFinish(@NonNull final Boolean status) {
        if (status) {
            mCallback.onSuccess();
        }
        else {
            mCallback.onError();
        }
    }
}
