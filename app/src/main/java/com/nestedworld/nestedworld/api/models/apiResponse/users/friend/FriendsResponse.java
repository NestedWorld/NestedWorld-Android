package com.nestedworld.nestedworld.api.models.apiResponse.users.friend;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.nestedworld.nestedworld.api.models.User;

import java.util.ArrayList;

public class FriendsResponse {
    @Expose
    public ArrayList<Friend> friends;

    public static class Friend {
        @Expose
        @SerializedName("user")
        public User info;
    }
}
