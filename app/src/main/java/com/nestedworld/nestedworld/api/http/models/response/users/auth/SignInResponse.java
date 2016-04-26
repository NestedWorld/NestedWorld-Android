package com.nestedworld.nestedworld.api.http.models.response.users.auth;

import com.google.gson.annotations.Expose;

/**
 * Simple model for mapping a json response
 */
public class SignInResponse {
    @Expose
    public String token;
}
