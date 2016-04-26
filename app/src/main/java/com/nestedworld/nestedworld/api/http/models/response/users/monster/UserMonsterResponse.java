package com.nestedworld.nestedworld.api.http.models.response.users.monster;

import com.google.gson.annotations.Expose;
import com.nestedworld.nestedworld.api.http.models.common.Monster;

import java.util.ArrayList;

/**
 * Simple model for mapping a json response
 */
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
