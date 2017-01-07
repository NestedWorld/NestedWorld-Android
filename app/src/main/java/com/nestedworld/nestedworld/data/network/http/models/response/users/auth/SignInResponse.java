package com.nestedworld.nestedworld.data.network.http.models.response.users.auth;

import com.google.gson.annotations.Expose;
import com.nestedworld.nestedworld.data.network.http.models.response.BaseHttpEntity;

/**
 * Simple model for mapping a json response
 */
public class SignInResponse extends BaseHttpEntity {
    @Expose
    public String token;
}
