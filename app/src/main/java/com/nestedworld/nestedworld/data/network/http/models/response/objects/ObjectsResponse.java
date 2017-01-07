package com.nestedworld.nestedworld.data.network.http.models.response.objects;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.nestedworld.nestedworld.data.database.models.ShopItem;

import java.util.List;

public class ObjectsResponse {

    @SerializedName("Objects")
    @Expose
    public List<ShopItem> objects;
}
