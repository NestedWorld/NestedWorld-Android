package com.nestedworld.nestedworld.network.http.models.response.geo.portal;

import com.google.gson.annotations.Expose;

import java.util.List;

public class PortalsResponse {
    @Expose
    public List<PortalResponse> portals;

    /*
    ** Inner class (used for parsing)
     */
    public static class PortalResponse {

        @Expose
        public List<Double> position;

        @Expose
        public String name;

        @Expose
        public String type;

        /*
        ** Utils
         */
        public com.nestedworld.nestedworld.database.models.Portal asPortal() {
            return new com.nestedworld.nestedworld.database.models.Portal(position.get(1), position.get(0), name, type);
        }
    }
}
