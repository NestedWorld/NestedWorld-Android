package com.nestedworld.nestedworld.api.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Simple model for mapping the "/user" response
 * generated with http://www.jsonschema2pojo.org/
 */
public class User {
    @SerializedName("token")
    @Expose
    private String token;

    /**
     * @return The token
     */
    public String getToken() {
        return token;
    }

    /**
     * @param token The token
     */
    public void setToken(String token) {
        this.token = token;
    }

}