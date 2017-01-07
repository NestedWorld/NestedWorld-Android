package com.nestedworld.nestedworld.data.network.http.models.response.attacks;

import com.google.gson.annotations.Expose;
import com.nestedworld.nestedworld.data.database.entities.Attack;
import com.nestedworld.nestedworld.data.network.http.models.response.BaseHttpResponse;

import java.util.List;

public class AttacksResponse extends BaseHttpResponse {
    @Expose
    public List<Attack> attacks;
}
