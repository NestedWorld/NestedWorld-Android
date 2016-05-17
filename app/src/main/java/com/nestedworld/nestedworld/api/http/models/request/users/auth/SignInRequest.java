package com.nestedworld.nestedworld.api.http.models.request.users.auth;

import com.google.gson.annotations.Expose;

public class SignInRequest {
    @Expose
    final String email;

    @Expose
    final String password;

    @Expose
    final String app_token;

    public SignInRequest(String email, String password, String appToken) {
        this.email = email;
        this.password = password;
        this.app_token = appToken;
    }
}
