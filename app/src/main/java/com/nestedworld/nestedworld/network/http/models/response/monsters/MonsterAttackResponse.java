package com.nestedworld.nestedworld.network.http.models.response.monsters;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.nestedworld.nestedworld.database.models.Attack;

import java.util.List;

public class MonsterAttackResponse {
    @Expose
    public List<MonsterAttack> attacks;

    public static class MonsterAttack {
        @Expose
        public Attack infos;

        @Expose
        @SerializedName("id")
        public long attackId;
    }
}
