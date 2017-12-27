package com.zhy.zooweb.dao.h2;

import java.util.List;
import java.util.Map;

public interface ZkCfgManager {

    boolean add(String id, String des, String connectStr, String sessionTimeOut);

    List<Map<String, Object>> query();

    List<Map<String, Object>> query(int page, int rows);

    boolean update(String id, String des, String connectStr, String sessionTimeOut);

    boolean delete(String id);

    Map<String, Object> findById(String id);

    long count();
}
