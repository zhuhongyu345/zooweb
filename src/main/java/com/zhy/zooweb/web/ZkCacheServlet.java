package com.zhy.zooweb.web;

import com.zhy.zooweb.dao.h2.ZkCfgManager;
import com.zhy.zooweb.dao.zookeeper.ZkCache;
import com.zhy.zooweb.dao.zookeeper.ZkManagerImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;


@WebServlet(name = "CacheServlet", urlPatterns = "/cache")
public class ZkCacheServlet extends HttpServlet {
    private static final Logger log = LoggerFactory.getLogger(ZkCacheServlet.class);
    private static final long serialVersionUID = 1L;
    @Resource
    private ZkCfgManager zkCfgManager;

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        for (Map<String, Object> m : zkCfgManager.query()) {
            log.info("ID : {},CONNECTSTR : {},SESSIONTIMEOUT : {}",
                    m.get("ID"), m.get("CONNECTSTR"), m.get("SESSIONTIMEOUT"));
            ZkCache.put(m.get("ID").toString(),
                    ZkManagerImpl.createZk().connect(m.get("CONNECTSTR").toString(),
                            Integer.parseInt(m.get("SESSIONTIMEOUT").toString())));
        }
        for (String key : ZkCache.get_cache().keySet()) {
            log.info("key : {} , zk : {}", key, ZkCache.get(key));
        }
    }
}
