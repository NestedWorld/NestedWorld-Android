package com.nestedworld.nestedworld.network.http.callback;

import android.accounts.NetworkErrorException;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import retrofit2.Call;
import retrofit2.Response;

public abstract class Callback<T> implements retrofit2.Callback<T> {

    @Override
    public void onResponse(Call<T> call, Response<T> response) {
        if (response.isSuccessful()) {
            onSuccess(response);
        } else {
            onError(KIND.SERVER, response);
        }
    }

    @Override
    public void onFailure(Call<T> call, Throwable t) {
        if (t instanceof NetworkErrorException) {
            onError(KIND.NETWORK, null);
        } else {
            onError(KIND.UNEXPECTED, null);
        }
    }

    public abstract void onSuccess(Response<T> response);

    public abstract void onError(@NonNull final KIND errorKind, @Nullable Response<T> response);

    public enum KIND {
        NETWORK,
        UNEXPECTED,
        SERVER,
    }

}
