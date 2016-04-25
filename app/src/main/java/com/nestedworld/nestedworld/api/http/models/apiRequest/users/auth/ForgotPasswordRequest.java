package com.nestedworld.nestedworld.api.http.models.apiRequest.users.auth;

public class ForgotPasswordRequest {
    final String email;

    public ForgotPasswordRequest(String email) {
        this.email = email;
    }
}
