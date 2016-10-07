package com.nestedworld.nestedworld.helpers.bimap;

import android.support.annotation.NonNull;

import java.util.HashMap;

public class BiMap<K, V> {

    private final HashMap<K, V> map = new HashMap<>();
    private final HashMap<V, K> invertedMap = new HashMap<>();

    public void put(@NonNull final K k, @NonNull final V v) {
        map.put(k, v);
        invertedMap.put(v, k);
    }

    public V getValueFromKey(@NonNull final K key) {
        return map.get(key);
    }

    public K getKeyFromValue(@NonNull final V value) {
        return invertedMap.get(value);
    }

    public Boolean containsValue(@NonNull final V value) {
        return map.containsValue(value);
    }

    public Boolean containsKey(@NonNull final K key) {
        return map.containsKey(key);
    }
}