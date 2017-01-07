package com.nestedworld.nestedworld.data.network.http.models.request.users.auth;

import android.support.annotation.Nullable;

import com.google.gson.annotations.Expose;

public class ForgotPasswordRequest {
    @Expose
    private final String email;

    public ForgotPasswordRequest(@Nullable final String email) {
        this.email = email;
    }
}
