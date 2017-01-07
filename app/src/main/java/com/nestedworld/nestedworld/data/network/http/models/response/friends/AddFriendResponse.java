package com.nestedworld.nestedworld.data.network.http.models.response.friends;


import com.google.gson.annotations.Expose;
import com.nestedworld.nestedworld.data.network.http.models.response.BaseHttpResponse;


public class AddFriendResponse extends BaseHttpResponse {
    @Expose
    public String message;
}
