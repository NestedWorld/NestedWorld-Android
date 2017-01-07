package com.nestedworld.nestedworld.data.network.http.models.response.geo.portal;

import com.google.gson.annotations.Expose;
import com.nestedworld.nestedworld.data.database.entities.Portal;
import com.nestedworld.nestedworld.data.network.http.models.response.BaseHttpEntity;

import java.util.List;

public class PortalsResponse extends BaseHttpEntity {
    @Expose
    public List<PortalResponse> portals;

    /*
    ** Inner class (used for parsing)
     */
    public static class PortalResponse extends BaseHttpEntity {

        @Expose
        public List<Double> position;

        @Expose
        public String name;

        @Expose
        public String type;
    }
}
