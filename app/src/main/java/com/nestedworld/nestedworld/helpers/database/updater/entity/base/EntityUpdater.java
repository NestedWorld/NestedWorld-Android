package com.nestedworld.nestedworld.helpers.database.updater.entity.base;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.nestedworld.nestedworld.helpers.database.updater.callback.OnEntityUpdated;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Response;

public abstract class EntityUpdater<T> extends Thread {
    protected final static String TAG = EntityUpdater.class.getSimpleName();

    private final Context mContext;
    private final OnEntityUpdated mCallback;

    /*
    ** Constructor
     */
    public EntityUpdater(@NonNull final Context context, @Nullable final OnEntityUpdated callback) {
        mContext = context;
        mCallback = callback;
    }

    /*
    ** Method every child will have to implement
     */
    @NonNull
    public abstract Call<T> getRequest();

    public abstract void updateEntity(@NonNull final Response<T> response);

    /*
    ** Thread implementation
     */
    public void run() {
        Call<T> request = getRequest();

        try {
            Response<T> response = request.execute();
            if (response != null && response.isSuccessful()) {
                updateEntity(response);
                onSuccess();
            } else {
                //We don't want to call the listener if the Thread has been interrupted
                if (isInterrupted()) {
                    onThreadInterrupted();
                } else {
                    onError(OnEntityUpdated.KIND.SERVER);
                }
            }
        } catch (IOException e) {
            request.cancel();
            e.printStackTrace();
        }
    }

    /*
    ** Utils or child
     */
    public Context getContext() {
        return mContext;
    }

    /*
    ** Private method
     */
    private void onThreadInterrupted() {
        //Do what you want
    }

    private void onError(@NonNull final OnEntityUpdated.KIND kind) {
        if (mCallback != null) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    mCallback.onError(kind);
                }
            });
        }
    }

    private void onSuccess() {
        if (mCallback != null) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    mCallback.onSuccess();
                }
            });
        }
    }
}
