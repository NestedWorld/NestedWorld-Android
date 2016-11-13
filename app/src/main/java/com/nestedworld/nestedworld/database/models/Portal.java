package com.nestedworld.nestedworld.database.models;

import android.support.annotation.ColorRes;

import com.nestedworld.nestedworld.R;
import com.orm.SugarRecord;

public class Portal extends SugarRecord {
    public double latitude;
    public double longitude;
    public String name;
    public String type;


    //Empty constructor for SugarRecord
    public Portal() {
        //Keep empty
    }

    /*
    ** Utils
     */
    public Portal(Double latitude, Double longitude, String name, String type) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.name = name;
        this.type = type;
    }

    /*
    ** utils
     */
    @ColorRes
    public int getColorType() {
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
