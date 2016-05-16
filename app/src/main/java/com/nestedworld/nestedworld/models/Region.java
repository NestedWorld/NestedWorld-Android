package com.nestedworld.nestedworld.models;

import com.google.gson.annotations.Expose;

public class Region {
    @Expose
    public String url;

    @Expose
    public String name;

    //Generated
    @Override
    public String toString() {
        return "Region{" +
                "url='" + url + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
