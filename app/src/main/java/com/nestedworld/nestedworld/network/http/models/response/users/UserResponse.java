package com.nestedworld.nestedworld.network.http.models.response.users;

import com.google.gson.annotations.Expose;
import com.nestedworld.nestedworld.database.models.User;

/**
 * Simple model for mapping a json response
 */
public class UserResponse {
    @Expose
    public User user;
}
