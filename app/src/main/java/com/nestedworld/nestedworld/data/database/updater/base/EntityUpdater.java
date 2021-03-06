package com.nestedworld.nestedworld.data.database.updater.base;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;

import com.google.gson.JsonSyntaxException;
import com.nestedworld.nestedworld.data.database.entities.DaoSession;
import com.nestedworld.nestedworld.data.database.implementation.NestedWorldDatabase;
import com.nestedworld.nestedworld.data.database.updater.callback.OnEntityUpdated;
import com.nestedworld.nestedworld.data.network.http.implementation.NestedWorldHttpApi;
import com.nestedworld.nestedworld.helpers.log.LogHelper;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Response;

public abstract class EntityUpdater<T> {
    protected final static String TAG = EntityUpdater.class.getSimpleName();

    /*
     * #############################################################################################
     * # Public method
     * #############################################################################################
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
     * #############################################################################################
     * # Method every child will have to implement
     * #############################################################################################
     */
    @NonNull
    protected abstract Call<T> getRequest();

    protected abstract void updateEntity(@NonNull final Response<T> response);

    /*
     * #############################################################################################
     * # Utils (for child)
     * #############################################################################################
     */
    @NonNull
    protected NestedWorldHttpApi getApi() {
        return NestedWorldHttpApi.getInstance();
    }

    @NonNull
    protected DaoSession getDatabase() {
        return NestedWorldDatabase.getInstance().getDataBase();
    }

    /*
     * #############################################################################################
     * # Internal method
     * #############################################################################################
     */
    private boolean makeRequest() {
        LogHelper.d(TAG, "makeRequest");
        final Call<T> request = getRequest();

        try {
            final Response<T> response = request.execute();
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
        } catch (JsonSyntaxException e) {
            LogHelper.d(TAG, "makeRequest > JsonSyntaxException: " + e);
            e.printStackTrace();
            return false;
        }
    }
}
