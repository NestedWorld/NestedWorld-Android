package com.nestedworld.nestedworld.database.models;

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

    @Expose
    @SerializedName("id")
    @Unique
    public Long attack_id;// the sql table will be called attackid (see sugarOrm doc)
    @Expose
    public String name;
    @Expose
    public String type;

    //Empty constructor for SugarRecord
    public Attack() {
        //Keep empty
    }

    public AttackType getType() {
        if (this.type != null) {
            switch (this.type) {
                case "attack":
                    return AttackType.ATTACK;
                case "attacksp":
                    return AttackType.ATTACK_SP;
                case "defense":
                    return AttackType.DEFENSE;
                case "defensesp":
                    return AttackType.DEFENSE_SP;
                default:
                    break;
            }
        }
        return AttackType.UNKNOWN;
    }

    //Generated
    @Override
    public String toString() {
        return "Attack{" +
                "attack_id=" + attack_id +
                ", name='" + name + '\'' +
                ", type='" + getType() + '\'' +
                '}';
    }

    public enum AttackType {
        ATTACK,
        ATTACK_SP,
        DEFENSE,
        DEFENSE_SP,
        OBJECT_USE,
        UNKNOWN
    }
}
