package com.nestedworld.nestedworld.api.callback;

import android.accounts.NetworkErrorException;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import retrofit.Response;
import retrofit.Retrofit;

public abstract class Callback<T> implements retrofit.Callback<T> {

    @Override
    public void onResponse(Response<T> response, Retrofit retrofit) {
        if (response.isSuccess()) {
            onSuccess(response, retrofit);
        } else {
            onError(KIND.SERVER, response);
        }
    }

    @Override
    public void onFailure(Throwable t) {
        if (t instanceof NetworkErrorException) {
            onError(KIND.NETWORK, null);
        } else {
            onError(KIND.UNEXPECTED, null);
        }
    }

    public abstract void onSuccess(Response<T> response, Retrofit retrofit);

    public abstract void onError(@NonNull final KIND errorKind, @Nullable Response<T> response);

    public enum KIND {
        NETWORK,
        UNEXPECTED,
        SERVER,
    }
}
