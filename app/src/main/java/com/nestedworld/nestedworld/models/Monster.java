package com.nestedworld.nestedworld.models;

import com.google.gson.annotations.Expose;

public class Monster {
    @Expose
    public int id;

    @Expose
    public String hp;

    @Expose
    public int defense;

    @Expose
    public int attack;

    @Expose
    public String name;

    @Expose
    public String sprite;

    //Generated
    @Override
    public String toString() {
        return "Monster{" +
                "id=" + id +
                ", hp='" + hp + '\'' +
                ", defense=" + defense +
                ", attack=" + attack +
                ", name='" + name + '\'' +
                ", sprite='" + sprite + '\'' +
                '}';
    }
}
