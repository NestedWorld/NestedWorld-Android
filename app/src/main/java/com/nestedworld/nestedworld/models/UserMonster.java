package com.nestedworld.nestedworld.models;

import com.google.gson.annotations.Expose;
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
    public String level;

    @Expose
    public String surname;

    @Expose
    public String experience;

    //Empty constructor for SugarRecord
    public UserMonster() {
        //Keep empty
    }

    public int fkmonster;//key for Monster<->UserMonster relationship

    public Monster info() {
        return Select.from(Monster.class).where(Condition.prop("monsterid").eq(fkmonster)).first();
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
}
