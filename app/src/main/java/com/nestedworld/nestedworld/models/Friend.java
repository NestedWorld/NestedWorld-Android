package com.nestedworld.nestedworld.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Friend {
    @Expose
    @SerializedName("user")
    public User info;

    //Generated
    @Override
    public String toString() {
        return "Friend{" +
                "info=" + info +
                '}';
    }
}
