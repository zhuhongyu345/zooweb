package com.zhy.zooweb.config;


import com.zhy.zooweb.dao.h2.ZkCfgManager;
import com.zhy.zooweb.dao.zookeeper.ZkCache;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class Init implements ApplicationListener<ContextRefreshedEvent> {

    @Resource
    ZkCfgManager zkCfgManager;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        System.out.println("*********************************init srping*********************************");

        ZkCache.init(zkCfgManager);
    }
}
