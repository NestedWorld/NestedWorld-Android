package com.nestedworld.nestedworld.api.models.apiResponse;

import com.google.gson.annotations.SerializedName;

/**
 * Simple model for mapping a json response
 */
public class ErrorResponse {
    @SerializedName("message")
    public String message;
}