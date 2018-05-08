package com.zhy.zooweb.dao.zookeeper;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryNTimes;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs.Perms;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Stat;

import java.text.SimpleDateFormat;
import java.util.*;

import static org.apache.zookeeper.ZooDefs.Ids.OPEN_ACL_UNSAFE;

public class ZkManagerImpl implements Watcher, ZkManager {

    private CuratorFramework zk;
    private final String ROOT = "/";
    private static final Log log = LogFactory.getLog(ZkManagerImpl.class);

    public static ZkManagerImpl createZk() {

        return new ZkManagerImpl();
    }

    public ZkManagerImpl connect(String host, int timeout) {
        try {
            if (null == zk) zk = CuratorFrameworkFactory.newClient(
                    host,
                    timeout,timeout,
                    new RetryNTimes(3, 5000));
            zk.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return this;
    }

    public List<String> getChildren(String path) {

        try {
            return zk.getChildren().forPath(path == null ? ROOT : path);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<String>();
    }

    public String getData(String path) {
        try {
            Stat s = zk.checkExists().forPath(path);
            if (s != null) {
                byte b[] = zk.getData().forPath(path);
                if (null == b) {
                    return "";
                }
                return new String(b);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Map<String, String> getNodeMeta(String nodePath) {
        Map<String, String> nodeMeta = new LinkedHashMap<String, String>();
        try {
            if (nodePath.length() == 0) {
                nodePath = ROOT;
            }
            Stat s = zk.checkExists().forPath(nodePath);
            if (s != null) {
                nodeMeta.put(Meta.aversion.toString(),
                        String.valueOf(s.getAversion()));
                nodeMeta.put(Meta.ctime.toString(),
                        timeParser(s.getCtime()));
                nodeMeta.put(Meta.cversion.toString(),
                        String.valueOf(s.getCversion()));
                nodeMeta.put(Meta.czxid.toString(),
                        String.valueOf(s.getCzxid()));
                nodeMeta.put(Meta.dataLength.toString(),
                        String.valueOf(s.getDataLength()));
                nodeMeta.put(Meta.ephemeralOwner.toString(),
                        String.valueOf(s.getEphemeralOwner()));
                nodeMeta.put(Meta.mtime.toString(),
                        timeParser(s.getMtime()));
                nodeMeta.put(Meta.mzxid.toString(),
                        String.valueOf(s.getMzxid()));
                nodeMeta.put(Meta.numChildren.toString(),
                        String.valueOf(s.getNumChildren()));
                nodeMeta.put(Meta.pzxid.toString(),
                        String.valueOf(s.getPzxid()));
                nodeMeta.put(Meta.version.toString(),
                        String.valueOf(s.getVersion()));
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e);
        }
        return nodeMeta;
    }

    public static String timeParser(long ts) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss,SSS");
        GregorianCalendar calendar = new GregorianCalendar(TimeZone.getDefault());
        calendar.setTimeInMillis(ts);
        return sdf.format(calendar.getTime());
    }

    public List<Map<String, String>> getACLs(String nodePath) {
        List<Map<String, String>> returnACLs = new ArrayList<Map<String, String>>();
        try {
            if (nodePath.length() == 0) {
                nodePath = ROOT;
            }
            Stat s = zk.checkExists().forPath(nodePath);
            if (s != null) {
                List<ACL> aclList = zk.getACL().forPath(nodePath);
                for (ACL acl : aclList) {
                    Map<String, String> aclMap = new LinkedHashMap<String, String>();
                    aclMap.put(Acl.scheme.toString(), acl.getId().getScheme());
                    aclMap.put(Acl.id.toString(), acl.getId().getId());
                    StringBuilder sb = new StringBuilder();
                    int perms = acl.getPerms();
                    boolean addedPerm = false;
                    if ((perms & Perms.READ) == Perms.READ) {
                        sb.append("Read");
                        addedPerm = true;
                    }
                    if (addedPerm) {
                        sb.append(", ");
                    }
                    if ((perms & Perms.WRITE) == Perms.WRITE) {
                        sb.append("Write");
                        addedPerm = true;
                    }
                    if (addedPerm) {
                        sb.append(", ");
                    }
                    if ((perms & Perms.CREATE) == Perms.CREATE) {
                        sb.append("Create");
                        addedPerm = true;
                    }
                    if (addedPerm) {
                        sb.append(", ");
                    }
                    if ((perms & Perms.DELETE) == Perms.DELETE) {
                        sb.append("Delete");
                        addedPerm = true;
                    }
                    if (addedPerm) {
                        sb.append(", ");
                    }
                    if ((perms & Perms.ADMIN) == Perms.ADMIN) {
                        sb.append("Admin");
                    }
                    aclMap.put(Acl.perms.toString(), sb.toString());
                    returnACLs.add(aclMap);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e);
        }
        return returnACLs;
    }

    public boolean createNode(String path, String nodeName, String data) {
        try {
            String p;
            if (ROOT.equals(path)) {
                p = path + nodeName;
            } else {
                p = path + "/" + nodeName;
            }
            Stat s = zk.checkExists().forPath(p);
            if (s == null) {
                zk.create().withMode(CreateMode.PERSISTENT).withACL(OPEN_ACL_UNSAFE).forPath(p, data.getBytes());
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e);
        }
        return false;
    }

    public boolean deleteNodes(List<String> nodePaths) {
        try {
            for (String nodePath : nodePaths) {
                zk.delete().deletingChildrenIfNeeded().forPath(nodePath);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e);
        }
        return false;
    }

    public boolean setData(String nodePath, String data) {
        try {
            zk.setData().forPath(nodePath, data.getBytes("utf-8"));
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e);
        }
        return false;
    }

    public void process(WatchedEvent arg0) {
    }
}
