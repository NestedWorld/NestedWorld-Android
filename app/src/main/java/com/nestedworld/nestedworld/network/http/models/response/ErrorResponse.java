package com.nestedworld.nestedworld.network.http.models.response;

import com.google.gson.annotations.SerializedName;

/**
 * Simple model for mapping a json response
 */
public class ErrorResponse {
    @SerializedName("message")
    public String message;
}
