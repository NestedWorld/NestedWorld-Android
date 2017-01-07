package com.nestedworld.nestedworld.data.database.models;

import android.support.annotation.ColorRes;

import com.nestedworld.nestedworld.R;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Unique;

@Entity()
public class Portal {
    public double latitude;

    public double longitude;

    public String name;

    public String type;

    @Id(autoincrement = true)
    @Unique
    private Long id;

    /*
    ** Utils
     */
    public Portal(final Double latitude, final Double longitude, final String name, final String type) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.name = name;
        this.type = type;
    }

    @Generated(hash = 2046645952)
    public Portal(double latitude, double longitude, String name, String type, Long id) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.name = name;
        this.type = type;
        this.id = id;
    }

    @Generated(hash = 1547652620)
    public Portal() {
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
                return R.color.monster_water;
            case "fire":
                return R.color.monster_fire;
            case "earth":
                return R.color.monster_earth;
            case "electric":
                return R.color.monster_electric;
            case "plant":
                return R.color.monster_plant;
            default:
                return R.color.black;
        }
    }

    public double getLatitude() {
        return this.latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return this.longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
