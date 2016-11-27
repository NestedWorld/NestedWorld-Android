package com.nestedworld.nestedworld.database.models;

import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.nestedworld.nestedworld.R;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Unique;

@Entity()
public class Monster {
    @Expose
    @SerializedName("id")
    @Unique
    public Long monsterId;
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
    @Id(autoincrement = true)
    @Unique
    private Long id;

    @Generated(hash = 1610346844)
    public Monster(Long monsterId, float hp, float defense, float attack, String name,
                   String enragedSprite, String baseSprite, String type, float speed, Long id) {
        this.monsterId = monsterId;
        this.hp = hp;
        this.defense = defense;
        this.attack = attack;
        this.name = name;
        this.enragedSprite = enragedSprite;
        this.baseSprite = baseSprite;
        this.type = type;
        this.speed = speed;
        this.id = id;
    }

    @Generated(hash = 173869498)
    public Monster() {
    }

    @Override
    public String toString() {
        return "Monster{" +
                "attack=" + attack +
                ", monsterId=" + monsterId +
                ", hp=" + hp +
                ", defense=" + defense +
                ", name='" + name + '\'' +
                ", enragedSprite='" + enragedSprite + '\'' +
                ", baseSprite='" + baseSprite + '\'' +
                ", type='" + type + '\'' +
                ", speed=" + speed +
                ", id=" + id +
                '}';
    }

    //Utils
    @ColorRes
    public int getElementColorResource() {
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

    @DrawableRes
    public int getElementImageResource() {
        if (type == null) {
            return R.color.black;
        }

        switch (type) {
            case "water":
                return R.drawable.elem_water;
            case "fire":
                return R.drawable.element_fire;
            case "earth":
                return R.drawable.element_earth;
            case "electric":
                return R.drawable.elem_electric;
            case "plant":
                return R.drawable.element_plant;
            default:
                return R.drawable.ic_help_outline_24dp;
        }
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getMonsterId() {
        return this.monsterId;
    }

    public void setMonsterId(Long monsterId) {
        this.monsterId = monsterId;
    }

    public float getHp() {
        return this.hp;
    }

    public void setHp(float hp) {
        this.hp = hp;
    }

    public float getDefense() {
        return this.defense;
    }

    public void setDefense(float defense) {
        this.defense = defense;
    }

    public float getAttack() {
        return this.attack;
    }

    public void setAttack(float attack) {
        this.attack = attack;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEnragedSprite() {
        return this.enragedSprite;
    }

    public void setEnragedSprite(String enragedSprite) {
        this.enragedSprite = enragedSprite;
    }

    public String getBaseSprite() {
        return this.baseSprite;
    }

    public void setBaseSprite(String baseSprite) {
        this.baseSprite = baseSprite;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public float getSpeed() {
        return this.speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }
}
