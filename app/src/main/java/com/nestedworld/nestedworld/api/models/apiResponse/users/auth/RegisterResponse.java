package com.nestedworld.nestedworld.api.models.apiResponse.users.auth;

import com.google.gson.annotations.Expose;
import com.nestedworld.nestedworld.api.models.apiResponse.users.UserResponse;

public class RegisterResponse {
    @Expose
    public UserResponse user;
}
