package com.zhy.zooweb.dao.zookeeper;

import java.util.List;
import java.util.Map;

public interface ZkManager {

	ZkManagerImpl connect(String host, int timeout);

	List<String> getChildren(String path);

	String getData(String path);

	Map<String, String> getNodeMeta(String nodePath);

	List<Map<String, String>> getACLs(String nodePath);
	
	boolean createNode(String path, String nodeName, String data);

	boolean deleteNode(String nodePath);

	boolean setData(String nodePath, String data);

	enum Meta {
		czxid, mzxid, ctime, mtime, version, cversion, aversion, ephemeralOwner, dataLength, numChildren, pzxid
	}

	enum Acl {
		scheme, id, perms
	}
}
