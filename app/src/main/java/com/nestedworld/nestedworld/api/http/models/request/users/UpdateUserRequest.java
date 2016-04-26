package com.nestedworld.nestedworld.api.http.models.request.users;

import com.google.gson.annotations.Expose;
import com.nestedworld.nestedworld.api.http.models.common.User;

public class UpdateUserRequest {
    @Expose
    public User user;
}
