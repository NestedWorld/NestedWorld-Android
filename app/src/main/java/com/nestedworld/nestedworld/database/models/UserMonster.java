package com.nestedworld.nestedworld.database.models;

import android.support.annotation.Nullable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.nestedworld.nestedworld.R;
import com.orm.SugarRecord;
import com.orm.dsl.Unique;
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
    @SerializedName("id")
    @Unique
    public Long userMonsterId;// the sql table will be called usermonsterid (see sugarOrm doc)

    @Expose
    public Monster infos;

    @Expose
    public Long level;
    public Long fkmonster;//key for Monster<->UserMonster relationship
    @Expose
    private String surname;
    @Expose
    private String experience;

    //Empty constructor for SugarRecord
    public UserMonster() {
        //Keep empty
    }

    @Nullable
    public Monster info() {
        if (infos == null) {
            infos = Select.from(Monster.class).where(Condition.prop("monster_id").eq(fkmonster)).first();
        }
        return infos;
    }

    //Generated
    @Override
    public String toString() {
        return "UserMonster{" +
                "userMonsterId=" + userMonsterId +
                ", info=" + infos +
                ", level=" + level +
                ", surname='" + surname + '\'' +
                ", experience='" + experience + '\'' +
                ", fkmonster=" + fkmonster +
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
