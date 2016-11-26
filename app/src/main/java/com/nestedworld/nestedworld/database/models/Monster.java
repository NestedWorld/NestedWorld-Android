package com.nestedworld.nestedworld.database.models;

import android.support.annotation.ColorRes;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.nestedworld.nestedworld.R;
import com.orm.SugarRecord;
import com.orm.annotation.Unique;

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
    public float hp;

    @Expose
    public float defense;

    @Expose
    public float attack;

    @Expose
    @Unique
    public String name;

    @Expose
    @SerializedName("enraged_sprite")
    public String enragedSprite;

    @Expose
    @SerializedName("base_sprite")
    public String baseSprite;

    @Expose
    public String type;

    @Expose
    public float speed;

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
                ", enragedSprite='" + enragedSprite + '\'' +
                ", baseSprite='" + baseSprite + '\'' +
                ", type='" + type + '\'' +
                ", speed='" + speed + '\'' +
                '}';
    }

    //Utils
    @ColorRes
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
