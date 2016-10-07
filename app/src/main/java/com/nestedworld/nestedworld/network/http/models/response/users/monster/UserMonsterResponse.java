package com.nestedworld.nestedworld.network.http.models.response.users.monster;

import com.google.gson.annotations.Expose;
import com.nestedworld.nestedworld.database.models.UserMonster;

import java.util.ArrayList;

/**
 * Simple model for mapping a json response
 */
public class UserMonsterResponse {
    @Expose
    public ArrayList<UserMonster> monsters;
}
