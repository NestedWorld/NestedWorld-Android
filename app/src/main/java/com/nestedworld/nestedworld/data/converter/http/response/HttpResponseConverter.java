package com.nestedworld.nestedworld.data.converter.http.response;

import android.support.annotation.NonNull;

import com.nestedworld.nestedworld.data.database.entities.base.BaseEntity;
import com.nestedworld.nestedworld.data.network.http.models.response.BaseHttpEntity;

/**
 * Used to convert a source into a target
 * @param <S> Response (source)
 * @param <T> Realm object (target)
 */
public interface HttpResponseConverter<S extends BaseHttpEntity, T extends BaseEntity> {
    /**
     * @param source the response to convert
     * @return the newly created object
     */
    @NonNull
    T convert(@NonNull final S source);
}