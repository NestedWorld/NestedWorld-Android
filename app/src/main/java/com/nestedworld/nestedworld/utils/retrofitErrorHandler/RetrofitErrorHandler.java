package com.nestedworld.nestedworld.utils.retrofitErrorHandler;

import android.content.Context;
import android.text.TextUtils;

import com.nestedworld.nestedworld.R;
import com.nestedworld.nestedworld.api.models.RestError;

import retrofit.RetrofitError;

public class RetrofitErrorHandler {
    private final static String TAG = "RetrofitErrorHandler";

    /*
    ** Public method
     */
    public static String getErrorMessage(final RetrofitError error, final Context context) {

        String errorMessage;

        switch (error.getKind()) {
            case NETWORK:
                errorMessage = getNetworkError(error, context);
                break;
            case UNEXPECTED:
                errorMessage = getUnexpectedError(error, context);
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
    private static String getNetworkError(final RetrofitError error, final Context context) {
        return context.getResources().getString(R.string.error_no_internet);
    }

    private static String getUnexpectedError(final RetrofitError error, final Context context) {
        return context.getResources().getString(R.string.error_unexpected);
    }

    private static String getConversionError(final RetrofitError error, final Context context) {
        return context.getResources().getString(R.string.error_conversion);
    }

    private static String getHttpError(final RetrofitError error, final Context context) {
        //The server should a json body (describe under RestError.class)

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
        return context.getResources().getString(R.string.error_unknow);
    }
}