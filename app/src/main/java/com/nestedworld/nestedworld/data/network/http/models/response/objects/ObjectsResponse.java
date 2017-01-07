package com.nestedworld.nestedworld.data.network.http.models.response.objects;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.nestedworld.nestedworld.data.database.entities.ShopItem;
import com.nestedworld.nestedworld.data.network.http.models.response.BaseHttpResponse;

import java.util.List;

public class ObjectsResponse extends BaseHttpResponse {

    @SerializedName("Objects")
    @Expose
    public List<ShopItem> objects;
}
