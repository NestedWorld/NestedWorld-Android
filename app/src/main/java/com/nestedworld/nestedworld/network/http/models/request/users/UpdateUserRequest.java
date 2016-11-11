package com.nestedworld.nestedworld.network.http.models.request.users;

import com.google.gson.annotations.Expose;
import com.nestedworld.nestedworld.database.models.Player;

public class UpdateUserRequest {
    @Expose
    public Player user;
}
