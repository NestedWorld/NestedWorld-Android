package com.nestedworld.nestedworld.network.http.models.response.attack;

import com.google.gson.annotations.Expose;
import com.nestedworld.nestedworld.database.models.Attack;

import java.util.ArrayList;

public class AttacksResponse {
    @Expose
    public ArrayList<Attack> attacks;
}
