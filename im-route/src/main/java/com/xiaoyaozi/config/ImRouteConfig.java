package com.xiaoyaozi.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * tip: yml参数配置项
 *
 * @author xiaoyaozi
 * createTime: 2021-03-26 15:32
 */
@Getter
@Configuration
public class ImRouteConfig {

    /**
     * zk上记录 服务ip的根节点
     */
    @Value("${zk.node.server}")
    private String zkServerNode;
    /**
     * zk地址
     */
    @Value("${zk.address}")
    private String zkAddress;
    /**
     * 连接超时时间 ms
     */
    @Value("${zk.timeout.connect}")
    private int connectTimeout;
    /**
     * seesion超时时间 ms
     */
    @Value("${zk.timeout.session}")
    private int sessionTimeout;

}
