package com.nestedworld.nestedworld.data.network.http.models.response.users.auth;

import com.google.gson.annotations.Expose;
import com.nestedworld.nestedworld.data.network.http.models.response.BaseHttpEntity;
import com.nestedworld.nestedworld.data.network.http.models.response.users.UserResponse;

/**
 * Simple model for mapping a json response
 */
public class RegisterResponse extends BaseHttpEntity {
    @Expose
    public UserResponse user;
}
