package com.xiaoyaozi.route;

import com.xiaoyaozi.config.ImRouteConfig;
import org.apache.curator.framework.CuratorFramework;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.concurrent.ThreadLocalRandom;

/**
 * tip: 随机路由策略
 *
 * @author xiaoyaozi
 * createTime: 2021-03-22 14:04
 */
@Component
@ConditionalOnProperty(prefix = "route", name = "type", havingValue = "random")
public class RandomRouteStrategy extends RouteStrategy {

    public RandomRouteStrategy(CuratorFramework zkClient, ImRouteConfig routeConfig) {
        super(zkClient, routeConfig.getZkServerNode());
    }
    @Override
    public String routeServerIp(Long key) {
        super.checkServerIsAvailable();
        return serverIpList.get(ThreadLocalRandom.current().nextInt(serverIpList.size()));
    }
}
