package com.xiaoyaozi.config;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * tip: zk配置
 *
 * @author xiaoyaozi
 * createTime: 2021-03-22 16:31
 */
@Configuration
public class ZkConfig {

    @Value("${zk.address}")
    private String zkAddress;
    @Value("${zk.timeout.connect}")
    private int connectTimeout;
    @Value("${zk.timeout.session}")
    private int sessionTimeout;
    @Value("${zk.node.server}")
    private String zkServerNode;

    @Bean
    public String zkServerNode() {
        return zkServerNode;
    }

    @Bean
    public CuratorFramework client() {
        // ExponentialBackoffRetry 重试策略 间隔时间是2的指数增长，比如第一次等待1s，第二次2s，第三次4s。。。
        CuratorFramework client = CuratorFrameworkFactory.builder()
                .connectString(zkAddress)
                .connectionTimeoutMs(connectTimeout)
                .sessionTimeoutMs(sessionTimeout)
                .retryPolicy(new ExponentialBackoffRetry(1000, 5))
                .build();
        client.start();
        return client;
    }

}
