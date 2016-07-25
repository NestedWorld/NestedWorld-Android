package com.nestedworld.nestedworld.network.http.models.request.users.auth;

import android.support.annotation.Nullable;

import com.google.gson.annotations.Expose;

public class ForgotPasswordRequest {
    @Expose
    final String email;

    public ForgotPasswordRequest(@Nullable final String email) {
        this.email = email;
    }
}
