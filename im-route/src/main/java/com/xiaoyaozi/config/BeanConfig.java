package com.xiaoyaozi.config;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * tip:
 *
 * @author xiaoyaozi
 * createTime: 2021-03-22 16:31
 */
@Configuration
public class BeanConfig {

    @Autowired
    private ImRouteConfig routeConfig;

    @Bean
    public CuratorFramework client() {
        // ExponentialBackoffRetry 重试策略 间隔时间是2的指数增长，比如第一次等待1s，第二次2s，第三次4s。。。
        CuratorFramework client = CuratorFrameworkFactory.builder()
                .connectString(routeConfig.getZkAddress())
                .connectionTimeoutMs(routeConfig.getConnectTimeout())
                .sessionTimeoutMs(routeConfig.getSessionTimeout())
                .retryPolicy(new ExponentialBackoffRetry(1000, 5))
                .build();
        client.start();
        return client;
    }

}
