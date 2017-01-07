package com.nestedworld.nestedworld.data.network.http.errorHandler;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.nestedworld.nestedworld.R;
import com.nestedworld.nestedworld.data.network.http.callback.NestedWorldHttpCallback;
import com.nestedworld.nestedworld.data.network.http.models.response.error.ErrorResponse;

import java.io.IOException;

import retrofit2.Response;

/**
 * Simple RetrofitError parser
 * it's return an explicit error message
 * if the server provide an error message he will be returned
 * if the server didn't provide any error message, a custom (static) message will be returned
 */
public final class RetrofitErrorHandler {
    private final static String TAG = RetrofitErrorHandler.class.getSimpleName();

    /*
     * #############################################################################################
     * # Constructor
     * #############################################################################################
     */
    private RetrofitErrorHandler() {
        //Empty constructor for avoiding this class to be construct
    }

    /*
     * #############################################################################################
     * # Public (static) method
     * #############################################################################################
     */
    @NonNull
    public static String getErrorMessage(@NonNull final Context context,
                                         @NonNull final NestedWorldHttpCallback.KIND errorType,
                                         @NonNull final String defaultMessage,
                                         @Nullable final Response response) {
        final String errorMessage = getErrorMessage(context, errorType, response);

        return errorMessage == null ? defaultMessage : (defaultMessage + ":" + errorMessage);
    }

    @Nullable
    public static String getErrorMessage(@NonNull final Context context,
                                         @NonNull final NestedWorldHttpCallback.KIND errorType,
                                         @Nullable final Response response) {
        //try to get error message from the response
        if (response != null) {
            try {
                //get the response as a string
                final String serverResponse = response.errorBody().string();

                try {
                    //try to convert the response as an ErrorResponse
                    final ErrorResponse errorResponse = new Gson().fromJson(serverResponse, ErrorResponse.class);
                    //check if we have a response
                    if (errorResponse != null && errorResponse.message != null) {
                        return errorResponse.message;
                    }
                } catch (JsonSyntaxException ignored) {
                    //The response cannot be cast as an errorResponse
                }
            } catch (IOException e) {
                //Server issue (the server don't follow the specification)
            }
        }

        switch (errorType) {
            case NETWORK:
                return getNetworkError(context);
            case SERVER:
                return getUnexpectedError(context);
            default:
                break;
        }
        return null;
    }

    /*
     * #############################################################################################
     * # Internal (static) method
     * #############################################################################################
     */
    @NonNull
    private static String getNetworkError(@NonNull final Context context) {
        return context.getResources().getString(R.string.error_no_internet);
    }

    @NonNull
    private static String getUnexpectedError(@NonNull final Context context) {
        return context.getResources().getString(R.string.error_conversion);
    }
}