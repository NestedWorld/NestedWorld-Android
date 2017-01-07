package com.nestedworld.nestedworld.data.network.http.models.response.users.auth;

import com.google.gson.annotations.Expose;
import com.nestedworld.nestedworld.data.network.http.models.response.BaseHttpResponse;
import com.nestedworld.nestedworld.data.network.http.models.response.users.UserResponse;

/**
 * Simple model for mapping a json response
 */
public class RegisterResponse extends BaseHttpResponse {
    @Expose
    public UserResponse user;
}
