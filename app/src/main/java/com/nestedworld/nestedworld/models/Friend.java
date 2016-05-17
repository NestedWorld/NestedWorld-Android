package com.nestedworld.nestedworld.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.orm.SugarRecord;

public class Friend extends SugarRecord {
    @Expose
    @SerializedName("user")
    public User info;

    //Empty constructor for SugarRecord
    public Friend() {

    }

    //Generated
    @Override
    public String toString() {
        return "Friend{" +
                "info=" + info +
                '}';
    }
}
