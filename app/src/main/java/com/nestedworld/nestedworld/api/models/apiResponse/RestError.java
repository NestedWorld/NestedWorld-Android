package com.nestedworld.nestedworld.api.models.apiResponse;

import com.google.gson.annotations.SerializedName;

/**
 * Simple model for mapping a json response
 */
class RestError {
    @SerializedName("status")
    public int status;

    @SerializedName("message")
    public String message;
}
