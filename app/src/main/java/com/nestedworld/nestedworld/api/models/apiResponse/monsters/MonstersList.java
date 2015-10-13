package com.nestedworld.nestedworld.api.models.apiResponse.monsters;


import com.google.gson.annotations.Expose;

import java.util.ArrayList;

public class MonstersList {
    @Expose
    public ArrayList<Monster> monsters;

    public class Monster {
        @Expose
        public String hp;

        @Expose
        public int defense;

        @Expose
        public int attack;

        @Expose
        public String name;
    }
}
