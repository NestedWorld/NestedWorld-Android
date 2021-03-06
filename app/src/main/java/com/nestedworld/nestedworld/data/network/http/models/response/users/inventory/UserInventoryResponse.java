package com.nestedworld.nestedworld.data.network.http.models.response.users.inventory;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.nestedworld.nestedworld.data.database.entities.UserItem;
import com.nestedworld.nestedworld.data.network.http.models.response.BaseHttpEntity;

import java.util.List;

public class UserInventoryResponse extends BaseHttpEntity {
    @Expose
    @SerializedName("inventory")
    public List<UserItem> objects;
}
