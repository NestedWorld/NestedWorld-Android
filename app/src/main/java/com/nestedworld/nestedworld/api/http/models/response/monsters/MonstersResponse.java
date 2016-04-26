package com.nestedworld.nestedworld.api.http.models.response.monsters;

import com.google.gson.annotations.Expose;
import com.nestedworld.nestedworld.api.http.models.common.Monster;

import java.util.ArrayList;

/**
 * Simple model for mapping a json response
 */
public class MonstersResponse {
    @Expose
    public ArrayList<Monster> monsters;
}
