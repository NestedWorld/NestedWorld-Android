package com.nestedworld.nestedworld.network.http.models.response.users.auth;

import com.google.gson.annotations.Expose;

/**
 * Simple model for mapping a json response
 */
public class SignInResponse {
    @Expose
    public String token;
}
