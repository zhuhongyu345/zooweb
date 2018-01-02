package com.zhy.zooweb.web;

import com.zhy.zooweb.dao.h2.ZkCfgManager;
import com.zhy.zooweb.dao.zookeeper.ZkCache;
import com.zhy.zooweb.dao.zookeeper.ZkManagerImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.*;
import java.util.regex.Pattern;


@Controller
@RequestMapping("/zkcfg")
public class ZkCfgController {
    private static final Logger log = LoggerFactory.getLogger(ZkCfgController.class);
    @Resource
    private ZkCfgManager zkCfgManager;

    @RequestMapping(value = "/queryZkCfg")
    @ResponseBody
    public Map<String, Object> queryZkCfg(@RequestParam(required = false) int page,
                                          @RequestParam(required = false) int rows) {
        try {
            log.info(new Date() + "");
            Map<String, Object> _map = new HashMap<String, Object>();
            List<Map<String, Object>> query = zkCfgManager.query(page, rows);
            for(Map<String, Object> q:query){
                q.put("SESSIONTIMEOUT",Integer.parseInt(q.get("SESSIONTIMEOUT").toString())/1000);
            }
            _map.put("rows", query);
            _map.put("total", zkCfgManager.count());
            return _map;
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
        }
        return null;
    }

    @RequestMapping(value = "/addZkCfg", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String addZkCfg(@RequestParam(required = false) String des,
                           @RequestParam(required = false) String connectstr,
                           @RequestParam(required = false) String sessiontimeout) {
        try {
            if (des == null || des.equals("")) des = "zookeeper";
            if (sessiontimeout == null || sessiontimeout.equals("")) {
                sessiontimeout = "3600000";
            } else {
                sessiontimeout = sessiontimeout + 000;
            }
            Integer.parseInt(sessiontimeout);
            if (!Pattern.compile("(\\d+\\.\\d+\\.\\d+\\.\\d+):(\\d+)").matcher(connectstr).find()) return "添加失败";
            String id = UUID.randomUUID().toString().replaceAll("-", "");
            if (zkCfgManager.add(id, des, connectstr, sessiontimeout))
                ZkCache.put(id, ZkManagerImpl.createZk().connect(connectstr, Integer.parseInt(sessiontimeout)));
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
            return "添加失败";
        }
        return "添加成功";
    }

    @RequestMapping(value = "/queryZkCfgById")
    @ResponseBody
    public Map<String, Object> queryZkCfg(@RequestParam(required = false) String id) {
        try {
            return zkCfgManager.findById(id);
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
        }
        return null;
    }

    @RequestMapping(value = "/updateZkCfg", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String updateZkCfg(@RequestParam() String id, @RequestParam(required = false) String des,
                              @RequestParam(required = false) String connectstr,
                              @RequestParam(required = false) String sessiontimeout) {
        try {
            if (des == null || des.equals("")) des = "zookeeper";
            if (sessiontimeout == null || sessiontimeout.equals("")) {
                sessiontimeout = "3600000";
            } else {
                sessiontimeout = sessiontimeout + 000;
            }
            Integer.parseInt(sessiontimeout);
            if (!Pattern.compile("(\\d+\\.\\d+\\.\\d+\\.\\d+):(\\d+)").matcher(connectstr).find()) return "保存失败";
            if (zkCfgManager.update(id, des, connectstr, sessiontimeout))
                ZkCache.put(id, ZkManagerImpl.createZk().connect(connectstr, Integer.parseInt(sessiontimeout)));
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
            return "保存失败";
        }
        return "保存成功";
    }

    @RequestMapping(value = "/delZkCfg", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String delZkCfg(@RequestParam() String id) {
        try {
            if (zkCfgManager.delete(id)) ZkCache.remove(id);
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
            return "删除失败";
        }
        return "删除成功";
    }
}
