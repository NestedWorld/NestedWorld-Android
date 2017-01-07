package com.nestedworld.nestedworld.data.network.http.callback;

import android.accounts.NetworkErrorException;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Response;

public abstract class NestedWorldHttpCallback<T> implements retrofit2.Callback<T> {

    /*
     * #############################################################################################
     * # Method every child will have to implement
     * #############################################################################################
     */
    public abstract void onSuccess(@NonNull final Response<T> response);

    public abstract void onError(@NonNull final KIND errorKind,
                                 @Nullable final Response<T> response);

    /*
     * #############################################################################################
     * # retrofit2.Callback<T> implementation
     * #############################################################################################
     */
    @Override
    public void onResponse(Call<T> call,
                           Response<T> response) {
        if (response != null && response.isSuccessful()) {
            onSuccess(response);
        } else {
            onError(KIND.SERVER, response);
        }
    }

    @Override
    public void onFailure(Call<T> call,
                          Throwable t) {
        if (t instanceof NetworkErrorException || t instanceof IOException) {
            onError(KIND.NETWORK, null);
        } else {
            onError(KIND.UNEXPECTED, null);
        }
    }

    public enum KIND {
        NETWORK,
        UNEXPECTED,
        SERVER,
    }
}
