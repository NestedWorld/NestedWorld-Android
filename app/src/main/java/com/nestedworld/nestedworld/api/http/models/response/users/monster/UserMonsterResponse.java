package com.nestedworld.nestedworld.api.http.models.response.users.monster;

import com.google.gson.annotations.Expose;
import com.nestedworld.nestedworld.models.UserMonster;

import java.util.ArrayList;

/**
 * Simple model for mapping a json response
 */
public class UserMonsterResponse {
    @Expose
    public ArrayList<UserMonster> monsters;
}
