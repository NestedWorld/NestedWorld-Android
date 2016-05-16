package com.nestedworld.nestedworld.api.http.models.request.users.auth;

import com.google.gson.annotations.Expose;

public class ForgotPasswordRequest {
    @Expose
    final String email;

    public ForgotPasswordRequest(String email) {
        this.email = email;
    }
}
