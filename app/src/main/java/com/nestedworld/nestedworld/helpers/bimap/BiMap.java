package com.nestedworld.nestedworld.helpers.bimap;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.HashMap;

public class BiMap<K, V> {

    private final HashMap<K, V> map = new HashMap<>();
    private final HashMap<V, K> invertedMap = new HashMap<>();

    /*
     * #############################################################################################
     * # Public method
     * #############################################################################################
     */
    public void put(@NonNull final K k,
                    @NonNull final V v) {
        map.put(k, v);
        invertedMap.put(v, k);
    }

    @Nullable
    public V getValueFromKey(@NonNull final K key) {
        return map.get(key);
    }

    @Nullable
    public K getKeyFromValue(@NonNull final V value) {
        return invertedMap.get(value);
    }

    public boolean containsValue(@NonNull final V value) {
        return map.containsValue(value);
    }
}