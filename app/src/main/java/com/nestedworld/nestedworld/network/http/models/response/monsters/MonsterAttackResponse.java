package com.nestedworld.nestedworld.network.http.models.response.monsters;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.nestedworld.nestedworld.database.models.Attack;

import java.util.ArrayList;

public class MonsterAttackResponse {
    @Expose
    public ArrayList<MonsterAttack> attacks;

    public static class MonsterAttack {
        @Expose
        public Attack infos;

        @Expose
        @SerializedName("id")
        public long attack_id;
    }
}
