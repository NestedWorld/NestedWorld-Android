package com.nestedworld.nestedworld.data.network.http.models.response.error;

import com.google.gson.annotations.SerializedName;
import com.nestedworld.nestedworld.data.network.http.models.response.BaseHttpResponse;

/**
 * Simple model for mapping a json response
 */
public class ErrorResponse extends BaseHttpResponse {
    @SerializedName("message")
    public String message;
}
