package com.nestedworld.nestedworld.network.http.models.response.places;

import com.google.gson.annotations.Expose;
import com.nestedworld.nestedworld.database.models.Place;

import java.util.List;

/**
 * Simple model for mapping a json response
 */
public class PlacesResponse {
    @Expose
    public List<Place> places;
}
