package com.nestedworld.nestedworld.api.models.apiRequest.users.auth;

public class RegisterRequest {
    final String email;
    final String password;
    final String pseudo;

    public RegisterRequest(String email, String password, String pseudo) {
        this.email = email;
        this.password = password;
        this.pseudo = pseudo;
    }
}
