package com.nestedworld.nestedworld.network.http.models.request.users.auth;

import android.support.annotation.Nullable;

import com.google.gson.annotations.Expose;

public class RegisterRequest {
    @Expose
    final String email;

    @Expose
    final String password;

    @Expose
    final String pseudo;

    public RegisterRequest(@Nullable final String email, @Nullable final String password, @Nullable final String pseudo) {
        this.email = email;
        this.password = password;
        this.pseudo = pseudo;
    }
}
