package com.nestedworld.nestedworld.api.http.models.request.users.auth;

public class ForgotPasswordRequest {
    final String email;

    public ForgotPasswordRequest(String email) {
        this.email = email;
    }
}
