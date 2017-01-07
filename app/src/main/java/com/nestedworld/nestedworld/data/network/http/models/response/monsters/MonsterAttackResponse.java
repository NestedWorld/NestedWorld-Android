package com.nestedworld.nestedworld.data.network.http.models.response.monsters;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.nestedworld.nestedworld.data.database.entities.Attack;
import com.nestedworld.nestedworld.data.network.http.models.response.BaseHttpResponse;

import java.util.List;

public class MonsterAttackResponse extends BaseHttpResponse {
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
