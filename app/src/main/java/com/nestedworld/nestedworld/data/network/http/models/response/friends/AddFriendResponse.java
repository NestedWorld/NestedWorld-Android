package com.nestedworld.nestedworld.data.network.http.models.response.friends;


import com.google.gson.annotations.Expose;
import com.nestedworld.nestedworld.data.network.http.models.response.BaseHttpEntity;


public class AddFriendResponse extends BaseHttpEntity {
    @Expose
    public String message;
}
