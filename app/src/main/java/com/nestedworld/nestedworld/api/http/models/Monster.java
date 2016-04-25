package com.nestedworld.nestedworld.api.http.models;

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
}
