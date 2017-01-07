package com.nestedworld.nestedworld.data.network.http.models.request.users;

import com.google.gson.annotations.Expose;
import com.nestedworld.nestedworld.data.database.models.session.Session;
import com.nestedworld.nestedworld.data.database.models.session.SessionData;

public class UpdateUserRequest {
    @Expose
    public SessionData user;
}
