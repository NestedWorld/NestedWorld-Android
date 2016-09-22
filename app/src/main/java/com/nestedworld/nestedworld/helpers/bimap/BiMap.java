package com.nestedworld.nestedworld.helpers.bimap;

import java.util.HashMap;

public class BiMap<K, V> {

    private final HashMap<K, V> map = new HashMap<>();
    private final HashMap<V, K> invertedMap = new HashMap<>();

    public void put(K k, V v) {
        map.put(k, v);
        invertedMap.put(v, k);
    }

    public V getValueFromKey(K key) {
        return map.get(key);
    }

    public K getKeyFromValue(V value) {
        return invertedMap.get(value);
    }

    public Boolean containsValue(V value) {
        return map.containsValue(value);
    }

    public Boolean containsKey(K key) {
        return map.containsKey(key);
    }
}