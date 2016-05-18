package com.nestedworld.nestedworld.network.http.models.response.users.friend;

import com.google.gson.annotations.Expose;
import com.nestedworld.nestedworld.models.Friend;

import java.util.ArrayList;

/**
 * Simple model for mapping a json response
 */
public class FriendsResponse {
    @Expose
    public ArrayList<Friend> friends;
}
