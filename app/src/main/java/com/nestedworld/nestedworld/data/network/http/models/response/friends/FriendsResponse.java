package com.nestedworld.nestedworld.data.network.http.models.response.friends;

import com.google.gson.annotations.Expose;
import com.nestedworld.nestedworld.data.database.entities.friend.Friend;

import java.util.List;

public class FriendsResponse {
    @Expose
    public List<Friend> friends;
}
