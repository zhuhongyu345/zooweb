package com.zhy.zooweb.dao.zookeeper;

import com.zhy.zooweb.dao.h2.ZkCfgManager;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ZkCache {

	private static Map<String, ZkManager> _cache = new ConcurrentHashMap<String, ZkManager>();

	public static void put(String key, ZkManager zk){
		_cache.put(key, zk);
	}

	public static ZkManager get(String key){
		return _cache.get(key);
	}
	
	public static void remove(String key){
		_cache.remove(key);
	}
	
	public static int size(){
		return _cache.size();
	}

	public static Map<String, ZkManager> get_cache() {
		return _cache;
	}

	public static void init(ZkCfgManager cfgManager){
		List<Map<String, Object>> list = cfgManager.query();
		for(Map<String , Object> m : list){
			ZkCache.put(m.get("ID").toString(), ZkManagerImpl.createZk().connect(m.get("CONNECTSTR").toString(),
					Integer.parseInt(m.get("SESSIONTIMEOUT").toString())));
		}
	}
	
}
