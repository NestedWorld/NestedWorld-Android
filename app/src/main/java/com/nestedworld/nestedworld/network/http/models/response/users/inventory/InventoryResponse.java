package com.nestedworld.nestedworld.network.http.models.response.users.inventory;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class InventoryResponse {

    @Expose
    @SerializedName("inventory")
    List<InventoryObject> objects;

    public final static class InventoryObject {
        @Expose
        @SerializedName("id")
        int objectId;

        @Expose
        String object;
    }
}
