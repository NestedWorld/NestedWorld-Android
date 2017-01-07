package com.nestedworld.nestedworld.data.network.http.models.response.users.monster;

import com.google.gson.annotations.Expose;
import com.nestedworld.nestedworld.data.database.models.UserMonster;

import java.util.List;

/**
 * Simple model for mapping a json response
 */
public class UserMonsterResponse {
    @Expose
    public List<UserMonster> monsters;
}
