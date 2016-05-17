package com.nestedworld.nestedworld.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.orm.SugarRecord;

public class Monster extends SugarRecord {
    @Expose
    @SerializedName("id")
    public int monster_id;

    @Expose
    public String hp;

    @Expose
    public int defense;

    @Expose
    public int attack;

    @Expose
    public String name;

    @Expose
    public String sprite;

    //Empty constructor for SugarRecord
    public Monster(){

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
