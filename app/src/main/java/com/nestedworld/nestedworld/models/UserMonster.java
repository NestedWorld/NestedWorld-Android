package com.nestedworld.nestedworld.models;

import com.google.gson.annotations.Expose;
import com.orm.SugarRecord;

public class UserMonster extends SugarRecord {
    @Expose
    public Monster infos;

    @Expose
    public String surname;

    //Empty constructor for SugarRecord
    public UserMonster() {

    }

    //Generated
    @Override
    public String toString() {
        return "UserMonster{" +
                "infos=" + infos +
                ", surname='" + surname + '\'' +
                '}';
    }
}
