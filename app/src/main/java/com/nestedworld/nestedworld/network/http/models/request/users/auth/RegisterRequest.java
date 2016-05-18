package com.nestedworld.nestedworld.network.http.models.request.users.auth;

import com.google.gson.annotations.Expose;

public class RegisterRequest {
    @Expose
    final String email;

    @Expose
    final String password;

    @Expose
    final String pseudo;

    public RegisterRequest(String email, String password, String pseudo) {
        this.email = email;
        this.password = password;
        this.pseudo = pseudo;
    }
}
