package com.nestedworld.nestedworld.api.http.models.request.users.auth;

public class SignInRequest {
    final String email;
    final String password;
    final String app_token;

    public SignInRequest(String email, String password, String app_token) {
        this.email = email;
        this.password = password;
        this.app_token = app_token;
    }
}
