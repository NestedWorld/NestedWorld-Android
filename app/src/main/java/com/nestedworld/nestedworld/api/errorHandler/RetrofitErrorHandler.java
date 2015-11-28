package com.nestedworld.nestedworld.api.errorHandler;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.nestedworld.nestedworld.R;
import com.nestedworld.nestedworld.api.models.apiResponse.RestError;

import retrofit.RetrofitError;

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
    public static String getErrorMessage(@NonNull final Context context, @NonNull final RetrofitError error, @NonNull final String defaultMessage) {

        String errorMessage;

        //check the error type and call the corespondent error parser
        switch (error.getKind()) {
            case NETWORK:
                errorMessage = getNetworkError(error, context);
                break;
            case UNEXPECTED:
                errorMessage = defaultMessage;
                break;
            case CONVERSION:
                errorMessage = getConversionError(error, context);
                break;
            case HTTP:
                errorMessage = getHttpError(error, context);
                break;
            default:
                throw new IllegalStateException("Unknown error kind: " + error.getKind(), error);
        }
        return errorMessage;
    }

    /*
    ** Utils
     */
    private static String getNetworkError(@NonNull final RetrofitError error, @NonNull final Context context) {
        return context.getResources().getString(R.string.error_no_internet);
    }

    private static String getConversionError(@NonNull final RetrofitError error, @NonNull final Context context) {
        return context.getResources().getString(R.string.error_conversion);
    }

    private static String getHttpError(@NonNull final RetrofitError error, @NonNull final Context context) {
        //The server should send a json body (describe under RestError.class)
        //The body should contain the error message so we'll try to get it

        RestError body = null;

        try {
            body = (RestError) error.getBodyAs(RestError.class);
        } catch (Exception ignored) {
            //can't parse response body
        }

        if (body != null && !TextUtils.isEmpty(body.message)) {
            return body.message;
        }

        //The server didn't send an error message
        return context.getResources().getString(R.string.error_unknown);
    }
}