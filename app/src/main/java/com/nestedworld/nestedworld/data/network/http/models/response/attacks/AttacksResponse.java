package com.nestedworld.nestedworld.data.network.http.models.response.attacks;

import com.google.gson.annotations.Expose;
import com.nestedworld.nestedworld.data.database.entities.Attack;
import com.nestedworld.nestedworld.data.network.http.models.response.BaseHttpEntity;

import java.util.List;

public class AttacksResponse extends BaseHttpEntity {
    @Expose
    public List<Attack> attacks;
}
