package com.nestedworld.nestedworld.data.converter.http.response.geo.portal;

import android.support.annotation.NonNull;

import com.nestedworld.nestedworld.data.converter.http.response.HttpResponseConverter;
import com.nestedworld.nestedworld.data.database.entities.Portal;
import com.nestedworld.nestedworld.data.network.http.models.response.geo.portal.PortalsResponse;

public class PortalResponseConverter implements HttpResponseConverter<PortalsResponse.PortalResponse, Portal> {

    /*
     * #############################################################################################
     * # HttpResponseConverter<PortalsResponse.PortalResponse,Portal> implementation
     * #############################################################################################
     */
    @NonNull
    @Override
    public Portal convert(@NonNull final PortalsResponse.PortalResponse source) {
        return new Portal(
                source.position.get(1),
                source.position.get(0),
                source.name,
                source.type);
    }
}
