package com.nestedworld.nestedworld.data.network.http.models.response.attacks;

import com.google.gson.annotations.Expose;
import com.nestedworld.nestedworld.data.database.models.Attack;

import java.util.List;

public class AttacksResponse {
    @Expose
    public List<Attack> attacks;
}
