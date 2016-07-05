package com.nestedworld.nestedworld.helpers.bimap;

import java.util.HashMap;

public class BiMap<K,V> {

    private HashMap<K,V> map = new HashMap<>();
    private HashMap<V,K> invertedMap = new HashMap<>();

    public void put(K k, V v) {
        map.put(k, v);
        invertedMap.put(v, k);
    }

    public HashMap<K, V> getMap() {
        return map;
    }

    public HashMap<V, K> getInvertedMap() {
        return invertedMap;
    }

}