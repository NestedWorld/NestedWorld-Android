package com.nestedworld.nestedworld.api.http.models.apiRequest.users;

import com.google.gson.annotations.Expose;
import com.nestedworld.nestedworld.api.http.models.User;

public class UpdateUserRequest {
    @Expose
    public User user;
}
