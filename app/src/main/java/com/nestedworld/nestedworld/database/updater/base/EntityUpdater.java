package com.nestedworld.nestedworld.database.updater.base;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;

import com.nestedworld.nestedworld.database.updater.callback.OnEntityUpdated;
import com.nestedworld.nestedworld.helpers.log.LogHelper;
import com.nestedworld.nestedworld.network.http.implementation.NestedWorldHttpApi;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Response;

public abstract class EntityUpdater<T> {
    protected final static String TAG = EntityUpdater.class.getSimpleName();
    private final NestedWorldHttpApi mNestedWorldHttpApi;

    /*
    ** Constructor
     */
    protected EntityUpdater() {
        mNestedWorldHttpApi = NestedWorldHttpApi.getInstance();
    }

    /*
    ** Method every child will have to implement
     */
    @NonNull
    protected abstract Call<T> getRequest();

    protected abstract void updateEntity(@NonNull final Response<T> response);

    /*
    ** Public method
     */
    @WorkerThread
    public boolean run() {
        return makeRequest();
    }

    public void start(@Nullable final OnEntityUpdated onEntityUpdated) {
        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... params) {
                return makeRequest();
            }

            @Override
            protected void onPostExecute(Boolean result) {
                if (onEntityUpdated != null) {
                    if (result) {
                        onEntityUpdated.onSuccess();
                    } else {
                        onEntityUpdated.onError(OnEntityUpdated.KIND.SERVER);
                    }
                }
            }
        }.execute();
    }

    /*
    ** Utils for child
     */
    protected NestedWorldHttpApi getApi() {
        return mNestedWorldHttpApi;
    }

    /*
    ** Internal method
     */
    private boolean makeRequest() {
        LogHelper.d(TAG, "makeRequest");
        Call<T> request = getRequest();

        try {
            Response<T> response = request.execute();
            if (response != null && response.isSuccessful()) {
                updateEntity(response);
                return true;
            } else {
                return false;
            }
        } catch (IOException e) {
            LogHelper.d(TAG, "makeRequest > IOException: " + e);
            e.printStackTrace();
            return false;
        }
    }
}
