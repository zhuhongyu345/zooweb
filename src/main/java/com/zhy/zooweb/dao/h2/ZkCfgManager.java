package com.zhy.zooweb.dao.h2;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Repository
public class ZkCfgManager {

    @Resource
    private JdbcTemplate jdbcTemplate;

    public boolean add(String des, String connectStr, String sessionTimeOut) {
        return jdbcTemplate.update("INSERT INTO ZK VALUES(?,?,?,?)", UUID.randomUUID().toString()
                .replaceAll("-", ""), des, connectStr, sessionTimeOut) != 0;
    }

    public List<Map<String, Object>> query() {
        return jdbcTemplate.queryForList("SELECT * FROM ZK");
    }

    public boolean update(String id, String des, String connectStr, String sessionTimeOut) {
        return jdbcTemplate.update("UPDATE ZK SET DES=?,CONNECTSTR=?,SESSIONTIMEOUT=? WHERE ID=?;"
                , des, connectStr, sessionTimeOut, id) != 0;
    }

    public boolean delete(String id) {
        return jdbcTemplate.update("DELETE ZK WHERE ID=?", id) != 0;
    }

    public Map<String, Object> findById(String id) {
        return jdbcTemplate.queryForMap("SELECT * FROM ZK WHERE ID = ?", id);
    }

    public List<Map<String, Object>> query(int page, int rows) {
        return jdbcTemplate.queryForList("SELECT * FROM ZK limit ?,?", (page - 1) * rows, rows);
    }

    public boolean add(String id, String des, String connectStr, String sessionTimeOut) {
        return jdbcTemplate.update("INSERT INTO ZK VALUES(?,?,?,?);", id, des, connectStr, sessionTimeOut) != 0;
    }

    public long count() {
        return (Long) jdbcTemplate.queryForMap("SELECT count(id) FROM ZK").get("count(id)");
    }
}
