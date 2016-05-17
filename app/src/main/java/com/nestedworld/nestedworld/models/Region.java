package com.nestedworld.nestedworld.models;

import com.google.gson.annotations.Expose;
import com.orm.SugarRecord;

/**
 * Simple model for :
 * - mapping a json response with Gson anotation
 * - mapping a sql table with SugarORM
 * /!\ Keep the default constructor empty (see sugarOrm doc)
 */
public class Region extends SugarRecord {
    @Expose
    public String url;

    @Expose
    public String name;

    //Empty constructor for SugarRecord
    public Region() {
        //keep empty
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
