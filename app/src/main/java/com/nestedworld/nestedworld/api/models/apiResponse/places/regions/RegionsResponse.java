package com.nestedworld.nestedworld.api.models.apiResponse.places.regions;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;

/**
 * Simple model for mapping a json response
 */
public class RegionsResponse {

    @Expose
    public ArrayList<Region> regions;

    public static class Region {
        @Expose
        public String url;

        @Expose
        public String name;
    }
}
