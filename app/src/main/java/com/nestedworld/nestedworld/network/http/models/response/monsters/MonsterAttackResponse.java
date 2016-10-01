package com.nestedworld.nestedworld.network.http.models.response.monsters;

import com.google.gson.annotations.Expose;
import com.nestedworld.nestedworld.models.Attack;

import java.util.ArrayList;
import java.util.List;

public class MonsterAttackResponse {
    @Expose
    public ArrayList<MonsterAttack> attacks;

    public static class MonsterAttack {
        @Expose
        public Attack infos;
    }
}
