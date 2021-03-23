package com.xiaoyaozi.route;

import org.apache.curator.framework.CuratorFramework;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicLong;

/**
 * tip: 轮询策略
 *
 * @author xiaoyaozi
 * createTime: 2021-03-22 15:28
 */
@Component
@ConditionalOnProperty(prefix = "route", name = "type", havingValue = "loop")
public class LoopRouteStrategy extends RouteStrategy {

    private final AtomicLong count = new AtomicLong();
    private boolean isPowerOfTwo;

    public LoopRouteStrategy(CuratorFramework zkClient, String zkServerNode) {
        super(zkClient, zkServerNode);
    }

    @Override
    public String routeServerIp(Long key) {
        super.checkServerIsAvailable();
        // 这里可以注意下，为什么 &：不需要取绝对值，%：需要取绝对值
        if (isPowerOfTwo) {
            return serverIpList.get((int) (count.getAndIncrement() & serverIpList.size() - 1));
        }
        return serverIpList.get((int) Math.abs(count.getAndIncrement() % serverIpList.size()));
    }

    @Override
    protected void updateServerIpList() {
        super.updateServerIpList();
        int size = serverIpList.size();
        isPowerOfTwo = ((size & -size) == size);
    }
}
