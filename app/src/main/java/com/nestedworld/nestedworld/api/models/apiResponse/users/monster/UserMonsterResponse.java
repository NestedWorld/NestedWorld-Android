package com.nestedworld.nestedworld.api.models.apiResponse.users.monster;

import com.google.gson.annotations.Expose;
import com.nestedworld.nestedworld.api.models.Monster;

import java.util.ArrayList;

public class UserMonsterResponse {
    @Expose
    public ArrayList<UserMonsters> monsters;

    public static class UserMonsters {
        @Expose
        public Monster infos;

        @Expose
        public String surname;
    }
}
