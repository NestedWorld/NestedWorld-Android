package com.nestedworld.nestedworld.api.models;

import com.google.gson.annotations.SerializedName;

public class RestError {
    @SerializedName("status")
    public int status;

    @SerializedName("message")
    public String message;
}
