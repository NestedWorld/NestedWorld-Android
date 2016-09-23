package com.nestedworld.nestedworld.network.http.models.response.monsters;

import com.google.gson.annotations.Expose;

import java.util.List;

public class MonsterAttackResponse {
    @Expose
    public List<String> attacks;
}
