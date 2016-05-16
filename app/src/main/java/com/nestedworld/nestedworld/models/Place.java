package com.nestedworld.nestedworld.models;

import com.google.gson.annotations.Expose;
import com.orm.SugarRecord;

import java.util.ArrayList;

public class Place extends SugarRecord {
    @Expose
    public String url;

    @Expose
    public String name;

    @Expose
    private ArrayList<Float> position;

    public Float latitude() {
        if (position != null && (position.size() >= 2)) {
            return position.get(1);
        }
        return 0.0f;
    }

    public Float longitude() {
        if (position != null && (position.size() >= 2)) {
            return position.get(0);
        }
        return 0.0f;
    }

    //Empty constructor for SugarRecord
    public Place() {

    }

    //Generated
    @Override
    public String toString() {
        return "Place{" +
                "url='" + url + '\'' +
                ", name='" + name + '\'' +
                ", position=" + position +
                '}';
    }
}