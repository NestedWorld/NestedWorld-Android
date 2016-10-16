package com.nestedworld.nestedworld.network.http.models.response.places.regions;

import com.google.gson.annotations.Expose;
import com.nestedworld.nestedworld.database.models.Region;

import java.util.List;

/**
 * Simple model for mapping a json response
 */
public class RegionsResponse {

    @Expose
    public List<Region> regions;
}
