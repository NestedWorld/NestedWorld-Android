package com.nestedworld.nestedworld.data.network.http.models.response.users.monster;

import com.google.gson.annotations.Expose;
import com.nestedworld.nestedworld.data.database.entities.UserMonster;
import com.nestedworld.nestedworld.data.network.http.models.response.BaseHttpResponse;

import java.util.List;

/**
 * Simple model for mapping a json response
 */
public class UserMonsterResponse extends BaseHttpResponse {
    @Expose
    public List<UserMonster> monsters;
}
