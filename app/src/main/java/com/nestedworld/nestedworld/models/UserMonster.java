package com.nestedworld.nestedworld.models;

import android.support.annotation.Nullable;

import com.google.gson.annotations.Expose;
import com.nestedworld.nestedworld.R;
import com.orm.SugarRecord;
import com.orm.query.Condition;
import com.orm.query.Select;

/**
 * Simple model for :
 * - mapping a json response with Gson anotation
 * - mapping a sql table with SugarORM
 * /!\ Keep the default constructor empty (see sugarOrm doc)
 */
public class UserMonster extends SugarRecord {

    @Expose
    public Monster infos;

    @Expose
    public Long level;

    @Expose
    public String surname;

    @Expose
    public String experience;

    //Empty constructor for SugarRecord
    public UserMonster() {
        //Keep empty
    }

    public Long fkmonster;//key for Monster<->UserMonster relationship

    @Nullable
    public Monster info() {
        if (infos == null) {
            infos = Select.from(Monster.class).where(Condition.prop("monsterid").eq(fkmonster)).first();
        }
        return infos;
    }

    //Generated
    @Override
    public String toString() {
        return "UserMonster{" +
                "infos=" + infos +
                ", level='" + level + '\'' +
                ", surname='" + surname + '\'' +
                ", experience='" + experience + '\'' +
                ", fkMonster=" + fkmonster +
                '}';
    }

    //Utils
    public int getColorResource() {
        Monster info = info();
        if (info == null) {
            return R.color.black;
        }
        return info.getColorResource();
    }
}
