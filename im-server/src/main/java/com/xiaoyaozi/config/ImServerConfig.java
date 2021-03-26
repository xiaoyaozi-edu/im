package com.xiaoyaozi.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * tip: yml参数配置项
 *
 * @author xiaoyaozi
 * createTime: 2021-03-26 09:50
 */
@Getter
@Configuration
public class ImServerConfig {

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

    /**
     * websocket监听端口
     */
    @Value("${im.server.port}")
    private Integer imServerPort;
    /**
     * 30000ms没有收到客户端心跳，关闭channel
     */
    @Value("${im.ping.timeout}")
    private Integer pingTimeout;
    /**
     * 最大重连次数
     */
    @Value("${im.reconnect.max-count}")
    private int reconnectMaxCount;

}
