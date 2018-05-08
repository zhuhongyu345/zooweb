package com.zhy.zooweb;

import java.util.HashMap;
import java.util.Map;

public class UniqueId {

    private static final Map<String, Integer> pathIds = new HashMap<>();
    private static volatile int curId = 0;

    synchronized public static int forPath(String path) {
        Integer id = pathIds.get(path);
        if (id == null) {
            id = ++curId;
            pathIds.put(path, id);
        }
        return id;
    }
}
