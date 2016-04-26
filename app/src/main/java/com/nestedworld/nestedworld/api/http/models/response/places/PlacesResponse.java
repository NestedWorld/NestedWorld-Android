package com.nestedworld.nestedworld.api.http.models.response.places;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;

/**
 * Simple model for mapping a json response
 */
public class PlacesResponse {
    @Expose
    public ArrayList<Place> places;

    public class Place {
        @Expose
        public String url;

        @Expose
        public String name;

        @Expose
        private ArrayList<Float> position;

        public Float latitude() {
            if (position != null && (position.size() >= 2)) {
                return position.get(1);
            }
            return 0.0f;
        }

        public Float longitude() {
            if (position != null && (position.size() >= 2)) {
                return position.get(0);
            }
            return 0.0f;
        }
    }
}
