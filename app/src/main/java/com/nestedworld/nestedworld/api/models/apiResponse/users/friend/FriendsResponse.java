package com.nestedworld.nestedworld.api.models.apiResponse.users.friend;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;

public class FriendsResponse {
    @Expose
    public ArrayList<Friend> friends;

    public static class Friend {
        @Expose
        public String name;

        public Friend(String name) {
            this.name = name;
        }

    }
}
