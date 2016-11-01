package com.nestedworld.nestedworld.network.http.models.response.object;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ShopObjectsResponse {

    @SerializedName("Objects")
    @Expose
    public List<ShopObject> objects;

    public final static class ShopObject {
        @Expose
        public String name;

        @Expose
        public String kind;

        @Expose
        public String power;

        @Expose
        public Boolean premium;

        @Expose
        public String description;

        @Expose
        public long price;

        @Expose
        public String image;
    }
}
