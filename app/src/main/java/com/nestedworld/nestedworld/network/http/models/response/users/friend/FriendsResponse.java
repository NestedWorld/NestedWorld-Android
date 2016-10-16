package com.nestedworld.nestedworld.network.http.models.response.users.friend;

import com.google.gson.annotations.Expose;
import com.nestedworld.nestedworld.database.models.Friend;

import java.util.List;

/**
 * Simple model for mapping a json response
 */
public class FriendsResponse {
    @Expose
    public List<Friend> friends;
}
