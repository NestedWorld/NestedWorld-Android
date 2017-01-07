package com.nestedworld.nestedworld.data.network.http.models.response.monsters;

import com.google.gson.annotations.Expose;
import com.nestedworld.nestedworld.data.database.entities.Monster;
import com.nestedworld.nestedworld.data.network.http.models.response.BaseHttpResponse;

import java.util.List;


/**
 * Simple model for mapping a json response
 */
public class MonstersResponse extends BaseHttpResponse {
    @Expose
    public List<Monster> monsters;
}
