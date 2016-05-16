package com.nestedworld.nestedworld.api.http.models.response.users;

import com.google.gson.annotations.Expose;
import com.nestedworld.nestedworld.models.User;

/**
 * Simple model for mapping a json response
 */
public class UserResponse {
    @Expose
    public User user;
}
