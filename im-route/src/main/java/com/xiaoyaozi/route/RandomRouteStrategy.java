package com.xiaoyaozi.route;

import com.xiaoyaozi.route.RouteStrategy;
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

    @Override
    protected String routeServerIp(Long key) {
        super.checkServerIsAvailable();
        return serverIpList.get(ThreadLocalRandom.current().nextInt(serverIpList.size()));
    }
}
