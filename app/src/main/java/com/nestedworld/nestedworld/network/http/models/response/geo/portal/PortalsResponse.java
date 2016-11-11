package com.nestedworld.nestedworld.network.http.models.response.geo.portal;

import com.google.gson.annotations.Expose;
import com.nestedworld.nestedworld.database.models.Portal;

import java.util.List;

public class PortalsResponse {
    @Expose
    public List<Portal> portals;
}
