package com.nestedworld.nestedworld.models;

import com.google.gson.annotations.Expose;
import com.orm.SugarRecord;

public class Region extends SugarRecord {
    @Expose
    public String url;

    @Expose
    public String name;

    //Empty constructor for SugarRecord
    public Region() {

    }

    //Generated
    @Override
    public String toString() {
        return "Region{" +
                "url='" + url + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
