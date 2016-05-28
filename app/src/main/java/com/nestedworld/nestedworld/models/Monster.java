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
public class Monster extends SugarRecord {
    @Expose
    @SerializedName("id")
    @Unique
    public Long monster_id;// the sql table will be called monsterid (see sugarOrm doc)

    @Expose
    public String hp;

    @Expose
    public int defense;

    @Expose
    public int attack;

    @Expose
    @Unique
    public String name;

    @Expose
    public String sprite;

    //Empty constructor for SugarRecord
    public Monster() {
        //Keep empty
    }

    //Generated
    @Override
    public String toString() {

        return "Monster{" +
                "monsterId=" + monster_id +
                ", hp='" + hp + '\'' +
                ", defense=" + defense +
                ", attack=" + attack +
                ", name='" + name + '\'' +
                ", sprite='" + sprite + '\'' +
                '}';
    }
}
