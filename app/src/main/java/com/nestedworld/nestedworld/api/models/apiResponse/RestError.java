package com.nestedworld.nestedworld.api.models.apiResponse;

import com.google.gson.annotations.SerializedName;

/**
 * Simple model for mapping the error response
 * generated with http://www.jsonschema2pojo.org/
 */
public class RestError {
    @SerializedName("status")
    public int status;

    @SerializedName("message")
    public String message;
}
