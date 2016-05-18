package com.nestedworld.nestedworld.network.http.models.response.places;

import com.google.gson.annotations.Expose;
import com.nestedworld.nestedworld.models.Place;

import java.util.ArrayList;

/**
 * Simple model for mapping a json response
 */
public class PlacesResponse {
    @Expose
    public ArrayList<Place> places;
}
