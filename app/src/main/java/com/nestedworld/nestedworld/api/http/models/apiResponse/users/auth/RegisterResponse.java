package com.nestedworld.nestedworld.api.http.models.apiResponse.users.auth;

import com.google.gson.annotations.Expose;
import com.nestedworld.nestedworld.api.http.models.apiResponse.users.UserResponse;

/**
 * Simple model for mapping a json response
 */
public class RegisterResponse {
    @Expose
    public UserResponse user;
}
