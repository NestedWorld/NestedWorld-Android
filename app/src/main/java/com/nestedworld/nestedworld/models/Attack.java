package com.nestedworld.nestedworld.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.orm.SugarRecord;
import com.orm.dsl.Unique;

/**
 * Simple model for :
 * - mapping a json response with Gson anotation
 * - mapping a sql table with SugarORM
 * /!\ Keep the default constructor empty (see sugarOrm doc)
 */
public class Attack extends SugarRecord {

    //Empty constructor for SugarRecord
    public Attack() {
        //Keep empty
    }

    @Expose
    @SerializedName("id")
    @Unique
    public Long attack_id;// the sql table will be called attackid (see sugarOrm doc)
    @Expose
    public String name;
    @Expose
    public String type;

    //Generated
    @Override
    public String toString() {
        return "Attack{" +
                "attack_id=" + attack_id +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}
