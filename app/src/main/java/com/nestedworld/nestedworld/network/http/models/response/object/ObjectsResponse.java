package com.nestedworld.nestedworld.network.http.models.response.object;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.nestedworld.nestedworld.database.models.ShopItem;

import java.util.List;

public class ObjectsResponse {

    @SerializedName("Objects")
    @Expose
    public List<ShopItem> objects;
}
