package com.nestedworld.nestedworld.network.http.models.response.users.inventory;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.nestedworld.nestedworld.database.models.UserItem;

import java.util.List;

public class UserInventoryResponse {
    @Expose
    @SerializedName("inventory")
    public List<UserItem> objects;
}
