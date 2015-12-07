package com.nestedworld.nestedworld.api.errorHandler;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.nestedworld.nestedworld.R;

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
    public static String getErrorMessage(@NonNull final Context context, @NonNull final Throwable t, @NonNull final String defaultMessage) {

        String errorMessage;

        //TODO check the error type and call the corespondent error parser see https://gist.github.com/koesie10/bc6c62520401cc7c858f
        errorMessage = t.getMessage();
        Log.d(TAG, errorMessage);

        return defaultMessage;
    }

    /*
    ** Utils
     */
    private static String getNetworkError(@NonNull final Throwable t, @NonNull final Context context) {
        return context.getResources().getString(R.string.error_no_internet);
    }

    private static String getConversionError(@NonNull final Throwable t, @NonNull final Context context) {
        return context.getResources().getString(R.string.error_conversion);
    }

    private static String getHttpError(@NonNull final Throwable t, @NonNull final Context context) {
        //The server should send a json body (describe under RestError.class)
        //The body should contain the error message so we'll try to get it

        //TODO récupérer le message d'erreur et le retourner

        //The server didn't send an error message
        return context.getResources().getString(R.string.error_unknown);
    }
}