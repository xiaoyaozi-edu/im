package com.xiaoyaozi.route;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * tip: 轮询策略
 *
 * @author xiaoyaozi
 * createTime: 2021-03-22 15:28
 */
@Component
@ConditionalOnProperty(prefix = "route", name = "type", havingValue = "loop")
public class LoopRouteStrategy extends RouteStrategy {

    private final AtomicInteger count = new AtomicInteger();

    @Override
    protected String routeServerIp(Long key) {
        super.checkServerIsAvailable();
        // 绝对值适应数字溢出
        return serverIpList.get(Math.abs(count.getAndAdd(1) % serverIpList.size()));
    }
}
