package com.nestedworld.nestedworld.database.models;

import com.google.gson.annotations.Expose;
import com.orm.SugarRecord;

import java.util.ArrayList;

/**
 * Simple model for :
 * - mapping a json response with Gson anotation
 * - mapping a sql table with SugarORM
 * /!\ Keep the default constructor empty (see sugarOrm doc)
 */
public class Place extends SugarRecord {
    @Expose
    public String url;

    @Expose
    public String name;

    @Expose
    private ArrayList<Float> position;

    //Empty constructor for SugarRecord
    public Place() {
        //keep empty
    }

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