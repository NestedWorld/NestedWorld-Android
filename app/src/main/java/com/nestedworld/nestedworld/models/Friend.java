package com.nestedworld.nestedworld.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.orm.SugarRecord;

/**
 * Simple model for :
 * - mapping a json response with Gson anotation
 * - mapping a sql table with SugarORM
 * /!\ Keep the default constructor empty (see sugarOrm doc)
 */
public class Friend extends SugarRecord {
    @Expose
    @SerializedName("user")
    public User info;

    //Empty constructor for SugarRecord
    public Friend() {
        //Keep empty
    }

    //Generated
    @Override
    public String toString() {
        return "Friend{" +
                "info=" + info +
                '}';
    }
}
