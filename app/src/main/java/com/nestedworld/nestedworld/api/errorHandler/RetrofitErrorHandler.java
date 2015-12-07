package com.nestedworld.nestedworld.api.errorHandler;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.nestedworld.nestedworld.R;
import com.nestedworld.nestedworld.api.callback.Callback;

import retrofit.Response;

/**
 * Simple RetrofitError parser
 * it's return an explicit error message
 * if the server provide an error message he will be returned
 * if the server didn't provide any error message, a custom (static) message will be returned
 */
public class RetrofitErrorHandler {
    private final static String TAG = RetrofitErrorHandler.class.getSimpleName();

    /*
    ** Public method
     */
    public static String getErrorMessage(@NonNull final Context context, @NonNull final Callback.KIND errorType, @NonNull final String defaultMessage, @Nullable Response response) {

        if (response != null) {
            //TODO essayer de parser le body
        }

        switch (errorType) {
            case NETWORK:
                return getNetworkError(context);
            case SERVER:
                return getUnexpectedError(context);
        }
        return defaultMessage;
    }

    /*
    ** Utils
     */
    private static String getNetworkError(@NonNull final Context context) {
        return context.getResources().getString(R.string.error_no_internet);
    }

    private static String getUnexpectedError(@NonNull final Context context) {
        return context.getResources().getString(R.string.error_conversion);
    }
}