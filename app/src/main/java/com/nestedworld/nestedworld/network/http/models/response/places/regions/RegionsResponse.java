package com.nestedworld.nestedworld.network.http.models.response.places.regions;

import com.google.gson.annotations.Expose;
import com.nestedworld.nestedworld.models.Region;

import java.util.ArrayList;

/**
 * Simple model for mapping a json response
 */
public class RegionsResponse {

    @Expose
    public ArrayList<Region> regions;
}
