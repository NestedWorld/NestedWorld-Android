package com.nestedworld.nestedworld.data.network.http.models.response.users;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.nestedworld.nestedworld.data.database.models.session.SessionData;

/**
 * Simple model for mapping a json response
 */
public class UserResponse {
    @Expose
    @SerializedName("user")
    public SessionData player;
}
