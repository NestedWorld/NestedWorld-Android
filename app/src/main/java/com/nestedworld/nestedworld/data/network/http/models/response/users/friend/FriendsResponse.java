package com.nestedworld.nestedworld.data.network.http.models.response.users.friend;

import com.google.gson.annotations.Expose;
import com.nestedworld.nestedworld.data.database.entities.friend.Friend;
import com.nestedworld.nestedworld.data.network.http.models.response.BaseHttpResponse;

import java.util.List;

/**
 * Simple model for mapping a json response
 */
public class FriendsResponse extends BaseHttpResponse {
    @Expose
    public List<Friend> friends;
}
