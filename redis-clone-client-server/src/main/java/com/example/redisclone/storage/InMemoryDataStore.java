package com.example.redisclone.storage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryDataStore {

    private final ConcurrentHashMap<String, String> stringStore = new ConcurrentHashMap<String, String>();
    private final ConcurrentHashMap<String, ConcurrentHashMap<String, String>> mapStore =
            new ConcurrentHashMap<String, ConcurrentHashMap<String, String>>();

    public void setString(String key, String value) {
        stringStore.put(key, value);
    }

    public String getString(String key) {
        return stringStore.get(key);
    }

    public String appendString(String key, String suffix) {
        return stringStore.merge(key, suffix, new java.util.function.BiFunction<String, String, String>() {
            @Override
            public String apply(String currentValue, String appendValue) {
                return currentValue + appendValue;
            }
        });
    }

    public boolean removeString(String key) {
        return stringStore.remove(key) != null;
    }

    public void mapSet(String mapName, String field, String value) {
        mapStore.computeIfAbsent(mapName, new java.util.function.Function<String, ConcurrentHashMap<String, String>>() {
            @Override
            public ConcurrentHashMap<String, String> apply(String ignored) {
                return new ConcurrentHashMap<String, String>();
            }
        }).put(field, value);
    }

    public String mapGet(String mapName, String field) {
        Map<String, String> map = mapStore.get(mapName);
        return map == null ? null : map.get(field);
    }

    public List<String> mapKeys(String mapName) {
        Map<String, String> map = mapStore.get(mapName);
        if (map == null || map.isEmpty()) {
            return Collections.emptyList();
        }
        return new ArrayList<String>(new TreeMap<String, String>(map).keySet());
    }

    public List<String> mapValues(String mapName) {
        Map<String, String> map = mapStore.get(mapName);
        if (map == null || map.isEmpty()) {
            return Collections.emptyList();
        }
        return new ArrayList<String>(new TreeMap<String, String>(map).values());
    }
}

