package com.nestedworld.nestedworld.api.models.apiResponse.users.auth;

import com.google.gson.annotations.Expose;

public class Register {
    @Expose
    public User user;

    public class User {
        @Expose
        public String email;

        @Expose
        public String pseudo;
    }
}
