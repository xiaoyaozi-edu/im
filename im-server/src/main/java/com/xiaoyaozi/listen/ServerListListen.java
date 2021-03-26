package com.xiaoyaozi.listen;

import com.xiaoyaozi.config.ImServerConfig;
import com.xiaoyaozi.manage.ImServerChannelManage;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.imps.CuratorFrameworkState;
import org.apache.zookeeper.Watcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * tip: 服务端节点变化
 *
 * @author xiaoyaozi
 * createTime: 2021-03-25 09:43
 */
@Slf4j
@Configuration
public class ServerListListen {

    @Autowired
    private ImServerConfig serverConfig;
    @Autowired
    private CuratorFramework zkClient;

    private Watcher watcher;

    public void registerServerListListen() {
        watcher = watchedEvent -> updateServerIpList();
        // 先注册一次
        createNoticeNode();
    }

    /**
     * tip: 更新服务端ip信息
     *
     * @author xiaoyaozi
     * createTime: 2021-03-23 11:24
     */
    private void updateServerIpList() {
        try {
            // 判断一下zk状态
            if (zkClient.getState() == CuratorFrameworkState.STARTED) {
                List<String> serverIpList = zkClient.getChildren().forPath(serverConfig.getZkServerNode());
                ImServerChannelManage.updateServerSocketConnect(serverIpList);
                log.info("服务端ip节点发生变化，变化后节点信息是：{}", serverIpList.toString());
            }
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
            // 判断一下zk状态
            if (zkClient.getState() == CuratorFrameworkState.STARTED) {
                zkClient.getChildren().usingWatcher(watcher).forPath(serverConfig.getZkServerNode());
            }
        } catch (Exception e) {
            log.error("zk创建监听服务节点出错", e);
        }
    }
}
