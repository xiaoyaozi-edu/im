package com.xiaoyaozi.server;

import com.xiaoyaozi.config.ImServerConfig;
import com.xiaoyaozi.constant.RedisConstant;
import com.xiaoyaozi.handle.ImServerInitializer;
import com.xiaoyaozi.listen.ServerListListen;
import com.xiaoyaozi.manage.ImServerChannelManage;
import com.xiaoyaozi.util.RedisUtil;
import com.xiaoyaozi.util.SpringUtil;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.DefaultThreadFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.CreateMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.stream.Collectors;

/**
 * tip: 启动服务
 *
 * @author xiaoyaozi
 * createTime: 2021-03-26 10:02
 */
@Slf4j
@Component
public class ImServerStart {

    @Autowired
    private ImServerConfig serverConfig;
    @Autowired
    private CuratorFramework zkClient;
    @Autowired
    private ServerListListen serverListListen;
    @Autowired
    private ApplicationContext applicationContext;

    @PostConstruct
    public void postConstruct() {
        // 激活springUtil工具类
        SpringUtil.setApplicationContext(applicationContext);
        // 启动服务
        this.startServer();
        // 注意：监听服务需要放在启动服务之后，防止互联
        serverListListen.registerServerListListen();

        // 钩子函数，关闭系统时释放各种资源
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            // 释放redis上连接信息
            Long count = RedisUtil.deleteList(ImServerChannelManage.CLIENT_CHANNEL_MAP.keySet().stream()
                    .map(m -> RedisConstant.ROUTE_PREFIX + m).collect(Collectors.toSet()));
            log.info("释放了redis上连接信息，count: {}", count);
            // 关闭zk连接
            zkClient.close();
            log.info("this machine success release snowflake ip, the snowflake ip is {}", "finalSnowflakeIp");
        }));
    }

    public void startServer() {
        ServerBootstrap serverBootstrap = new ServerBootstrap()
                .group(new NioEventLoopGroup(1, new DefaultThreadFactory("boss")),
                        new NioEventLoopGroup(0, new DefaultThreadFactory("work-connect-client")))
                .channel(NioServerSocketChannel.class)
                .childHandler(new ImServerInitializer());
        try {
            ChannelFuture future = serverBootstrap.bind(serverConfig.getImServerPort()).sync();
            if (future.isSuccess()) {
                log.info("服务端启动成功");
                registerServerIpToZk();
            } else {
                log.error("服务端启动失败", future.cause());
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * tip: 注册ip到zk节点
     *
     * @author xiaoyaozi
     * createTime: 2021-03-25 09:48
     */
    private void registerServerIpToZk() {
        try {
            String hostAddress = InetAddress.getLocalHost().getHostAddress();
            MetaspaceConstant.HOST_PORT_ADDRESS = hostAddress + ":" + serverConfig.getImServerPort();
            zkClient.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL)
                    .forPath(serverConfig.getZkServerNode() + "/" + MetaspaceConstant.HOST_PORT_ADDRESS);
        } catch (UnknownHostException e) {
            log.error("获取本机ip地址出错", e);
        } catch (Exception e) {
            log.error("服务端注册到zk出错", e);
        }
    }
}
