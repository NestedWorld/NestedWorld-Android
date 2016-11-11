package com.nestedworld.nestedworld.network.http.models.response.users;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.nestedworld.nestedworld.database.models.Player;

/**
 * Simple model for mapping a json response
 */
public class UserResponse {
    @Expose
    @SerializedName("user")
    public Player player;
}
