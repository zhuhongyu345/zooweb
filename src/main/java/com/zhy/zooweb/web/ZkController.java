package com.zhy.zooweb.web;

import com.zhy.zooweb.dao.zookeeper.ZkCache;
import com.zhy.zooweb.model.Tree;
import com.zhy.zooweb.model.TreeRoot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/zk")
public class ZkController {
    private static final Logger logger = LoggerFactory.getLogger(ZkController.class);

    @RequestMapping(value = "/queryZnodeInfo")
    @ResponseBody
    public Map<String, Object> queryzNodeInfo(@RequestParam(required = false) String path,
                                              @RequestParam() String cacheId) {
        Map<String, Object> model = new HashMap<String, Object>();
        try {
            path = URLDecoder.decode(path, "utf-8");
            logger.info("queryzNodeInfo : " + path);
            if (path != null) {
                model.put("data", ZkCache.get(cacheId).getData(path));
                model.put("arr", ZkCache.get(cacheId).getNodeMeta(path));
                model.put("acls", ZkCache.get(cacheId).getACLs(path));
                model.put("path", path);
                model.put("cacheId", cacheId);
            }
        } catch (Exception e) {
            logger.error("", e);
        }
        return model;
    }


    @RequestMapping(value = "/queryZnode")
    @ResponseBody
    public List<Tree> query(@RequestParam(required = false) String id, @RequestParam(required = false) String path,
                            @RequestParam() String cacheId) {
        logger.info("id : {} | path : {} ", id, path);
        logger.info("path : {}", path);
        logger.info("cacheId : {}", cacheId);
        TreeRoot root = new TreeRoot();
        if (path != null) {
            if ("/".equals(path)) {
                root.remove(0);
                List<String> pathList = ZkCache.get(cacheId).getChildren(null);
                logger.info("list {}", pathList);
                for (String p : pathList) {
                    Map<String, Object> atr = new HashMap<String, Object>();
                    atr.put("path", "/" + p);
                    Tree tree = new Tree(0, p, Tree.STATE_CLOSED, null, atr);
                    root.add(tree);
                }
            } else {
                root.remove(0);
                try {
                    path = URLDecoder.decode(path, "utf-8");
                } catch (UnsupportedEncodingException e) {
                    logger.error("", e);
                }
                List<String> pathList = ZkCache.get(cacheId).getChildren(path);
                for (String p : pathList) {
                    Map<String, Object> atr = new HashMap<String, Object>();
                    atr.put("path", path + "/" + p);
                    Tree tree = new Tree(0, p, Tree.STATE_CLOSED, null, atr);
                    root.add(tree);
                }
            }
        }
        return root;
    }

    @RequestMapping(value = "/saveData", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String saveData(@RequestParam() String path, @RequestParam() String data, @RequestParam() String cacheId) {
        try {
            logger.info("data:{}", data);
            return ZkCache.get(cacheId).setData(path, data) ? "保存成功" : "保存失败";
        } catch (Exception e) {
            logger.info("Error : {}", e.getMessage(), e);
            return "保存失败! Error : " + e.getMessage();
        }

    }

    @RequestMapping(value = "/createNode", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String createNode(@RequestParam() String path, @RequestParam() String nodeName,
                             @RequestParam() String cacheId) {
        try {
            logger.info("path:{}", path);
            logger.info("nodeName:{}", nodeName);
            return ZkCache.get(cacheId).createNode(path, nodeName, "") ? "保存成功" : "保存失败";
        } catch (Exception e) {
            logger.info("Error : {}", e.getMessage(), e);
            return "保存失败! Error : " + e.getMessage();
        }

    }

    @RequestMapping(value = "/deleteNode", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String deleteNode(@RequestParam() String path, @RequestParam() String cacheId) {
        try {
            logger.info("path:{}", path);
            return ZkCache.get(cacheId).deleteNode(path) ? "删除成功" : "删除失败";
        } catch (Exception e) {
            logger.info("Error : {}", e.getMessage(), e);
            return "删除失败! Error : " + e.getMessage();
        }
    }
}
