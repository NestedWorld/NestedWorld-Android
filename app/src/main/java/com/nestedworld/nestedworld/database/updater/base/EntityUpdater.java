package com.nestedworld.nestedworld.database.updater.base;

import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.WorkerThread;
import android.view.View;

import com.nestedworld.nestedworld.database.updater.callback.OnEntityUpdated;
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
    public EntityUpdater(@NonNull final Context context) {
        mNestedWorldHttpApi = NestedWorldHttpApi.getInstance(context);
    }

    /*
    ** Method every child will have to implement
     */
    @NonNull
    public abstract Call<T> getRequest();

    public abstract void updateEntity(@NonNull final Response<T> response);

    /*
    ** Public method
     */
    @WorkerThread
    public boolean update() {
        return makeRequest();
    }

    public void update(@NonNull final OnEntityUpdated onEntityUpdated) {
        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... params) {
                return makeRequest();
            }

            @Override
            protected void onPostExecute(Boolean result) {
                if (result) {
                    onEntityUpdated.onSuccess();
                } else {
                    onEntityUpdated.onError(OnEntityUpdated.KIND.SERVER);
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
            e.printStackTrace();
            return false;
        }
    }
}
