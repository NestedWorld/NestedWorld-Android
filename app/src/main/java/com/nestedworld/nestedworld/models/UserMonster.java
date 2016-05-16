package com.nestedworld.nestedworld.models;

import com.google.gson.annotations.Expose;

public class UserMonster {
    @Expose
    public Monster infos;

    @Expose
    public String surname;

    //Generated
    @Override
    public String toString() {
        return "UserMonster{" +
                "infos=" + infos +
                ", surname='" + surname + '\'' +
                '}';
    }
}
