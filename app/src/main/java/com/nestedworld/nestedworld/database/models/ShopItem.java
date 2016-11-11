package com.nestedworld.nestedworld.database.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.orm.SugarRecord;

public class ShopItem extends SugarRecord {

    @Expose
    @SerializedName("id")
    public long shopItemId;//

    @Expose
    public String name;//

    @Expose
    public String kind;//

    @Expose
    public String power;//

    @Expose
    public Boolean premium;//

    @Expose
    public String description;//

    @Expose
    public long price;//

    @Expose
    public String image;//

    /*
    ** Constructor
     */
    //Empty constructor for SugarRecord
    public ShopItem() {
        //keep empty
    }

    /*
    ** Generated
     */
    @Override
    public String toString() {
        return "ShopItem{" +
                "description='" + description + '\'' +
                ", name='" + name + '\'' +
                ", kind='" + kind + '\'' +
                ", power='" + power + '\'' +
                ", premium=" + premium +
                ", price=" + price +
                ", image='" + image + '\'' +
                '}';
    }
}
