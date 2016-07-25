package com.nestedworld.nestedworld.network.http.models.request.friends;

import android.support.annotation.NonNull;

import com.google.gson.annotations.Expose;

public class AddFriendRequest {
    @Expose
    String pseudo;

    public AddFriendRequest(@NonNull final String pseudo) {
        this.pseudo = pseudo;
    }
}
