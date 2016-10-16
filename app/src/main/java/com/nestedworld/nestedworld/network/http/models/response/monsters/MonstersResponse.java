package com.nestedworld.nestedworld.network.http.models.response.monsters;

import com.google.gson.annotations.Expose;
import com.nestedworld.nestedworld.database.models.Monster;

import java.util.List;


/**
 * Simple model for mapping a json response
 */
public class MonstersResponse {
    @Expose
    public List<Monster> monsters;
}
