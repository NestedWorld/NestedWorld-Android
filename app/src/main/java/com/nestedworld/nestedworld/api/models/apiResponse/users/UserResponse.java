package com.nestedworld.nestedworld.api.models.apiResponse.users;

import com.google.gson.annotations.Expose;
import com.nestedworld.nestedworld.api.models.User;

/**
 * Simple model for mapping a json response
 */
public class UserResponse {
    @Expose
    public User user;
}
