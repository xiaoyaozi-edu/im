package com.xiaoyaozi.route;

import com.xiaoyaozi.enums.ImExceptionStatus;
import com.xiaoyaozi.exception.ImException;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.Watcher;

import java.util.List;

/**
 * tip: 路由策略
 *
 * @author xiaoyaozi
 * createTime: 2021-03-22 14:01
 */
@Slf4j
public abstract class RouteStrategy {

    protected List<String> serverIpList = null;
    private final Watcher watcher;
    private final CuratorFramework zkClient;
    private final String zkServerNode;

    public RouteStrategy(CuratorFramework zkClient, String zkServerNode) {
        this.zkClient = zkClient;
        this.zkServerNode = zkServerNode;
        watcher = watchedEvent -> updateServerIpList();
        // 启动时先更新一次节点
        updateServerIpList();
    }

    /**
     * tip: 根据key路由到某个服务器
     *
     * @param key key
     * @return String 服务器ip
     * @author xiaoyaozi
     * createTime: 2021-03-23 00:20
     */
    public abstract String routeServerIp(Long key);

    /**
     * tip: 校验是否有服务可供使用
     *
     * @author xiaoyaozi
     * createTime: 2021-03-23 11:56
     */
    protected void checkServerIsAvailable() {
        if (serverIpList == null || serverIpList.size() == 0) {
            throw ImException.exception(ImExceptionStatus.SERVER_NOT_AVAILABLE);
        }
    }

    /**
     * tip: 更新服务端ip信息
     *
     * @author xiaoyaozi
     * createTime: 2021-03-23 11:24
     */
    protected void updateServerIpList() {
        try {
            serverIpList = zkClient.getChildren().forPath(zkServerNode);
            log.info("服务端ip节点发生变化，变化后节点信息是：{}", serverIpList.toString());
        } catch (Exception e) {
            log.error("获取服务端ip节点出错", e);
        }
        // zk监听：一次注册，只会触发一次，只能在这里再次注册
        createNoticeNode();
    }

    /**
     * tip: 创建监听节点
     *
     * @author xiaoyaozi
     * createTime: 2021-03-23 11:53
     */
    private void createNoticeNode() {
        try {
            zkClient.getChildren().usingWatcher(watcher).forPath(zkServerNode);
        } catch (Exception e) {
            log.error("zk创建监听服务节点出错", e);
        }
    }
}
