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
    private ZkCfgManager zkCfgManager;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        ZkCache.init(zkCfgManager);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ignored) {
                }
                System.out.println("******************************************************************");
                System.out.println("******************************************************************");
                System.out.println("应用地址：127.0.0.1:9345");
                System.out.println("应用地址：127.0.0.1:9345");
                System.out.println("应用地址：127.0.0.1:9345");
                System.out.println("******************************************************************");
                System.out.println("******************************************************************");
            }
        }).start();
    }
}
