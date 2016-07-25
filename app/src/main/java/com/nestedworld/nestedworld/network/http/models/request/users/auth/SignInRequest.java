package com.nestedworld.nestedworld.network.http.models.request.users.auth;

import android.support.annotation.Nullable;

import com.google.gson.annotations.Expose;

public class SignInRequest {
    @Expose
    final String email;

    @Expose
    final String password;

    @Expose
    final String app_token;

    public SignInRequest(@Nullable final String email, @Nullable final String password, @Nullable final String appToken) {
        this.email = email;
        this.password = password;
        this.app_token = appToken;
    }
}
