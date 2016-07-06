package com.nestedworld.nestedworld.network.http.models.request.friends;

import com.google.gson.annotations.Expose;

public class AddFriendRequest {
    @Expose
    String pseudo;

    public AddFriendRequest(String pseudo) {
        this.pseudo = pseudo;
    }
}
