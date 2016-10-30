package com.nestedworld.nestedworld.database.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.nestedworld.nestedworld.R;
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
    public Long monsterId;// the sql table will be called monsterid (see sugarOrm doc)

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
    public String enraged_sprite;

    @Expose
    public String base_sprite;

    @Expose
    public String type;

    @Expose
    public String speed;

    //Empty constructor for SugarRecord
    public Monster() {
        //Keep empty
    }

    //Generated
    @Override
    public String toString() {
        return "Monster{" +
                "attack=" + attack +
                ", monsterId=" + monsterId +
                ", hp='" + hp + '\'' +
                ", defense=" + defense +
                ", name='" + name + '\'' +
                ", enraged_sprite='" + enraged_sprite + '\'' +
                ", base_sprite='" + base_sprite + '\'' +
                ", type='" + type + '\'' +
                ", speed='" + speed + '\'' +
                '}';
    }

    //Utils
    public int getColorResource() {
        if (type == null) {
            return R.color.black;
        }

        switch (type) {
            case "water":
                return R.color.holo_blue_light;
            case "fire":
                return R.color.holo_red_light;
            case "earth":
                return R.color.DarkKhaki;
            case "electric":
                return R.color.holo_orange_light;
            case "plant":
                return R.color.holo_green_light;
            default:
                return R.color.black;
        }
    }
}
