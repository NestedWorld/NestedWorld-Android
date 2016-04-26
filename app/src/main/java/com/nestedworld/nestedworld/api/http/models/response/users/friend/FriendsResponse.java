package com.nestedworld.nestedworld.api.http.models.response.users.friend;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.nestedworld.nestedworld.api.http.models.common.User;

import java.util.ArrayList;

/**
 * Simple model for mapping a json response
 */
public class FriendsResponse {
    @Expose
    public ArrayList<Friend> friends;

    public static class Friend {
        @Expose
        @SerializedName("user")
        public User info;
    }
}