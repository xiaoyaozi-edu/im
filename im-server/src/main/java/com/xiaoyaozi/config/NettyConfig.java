package com.xiaoyaozi.config;

import com.xiaoyaozi.server.ImServerInitializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.DefaultThreadFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.CreateMode;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * tip:
 *
 * @author xiaoyaozi
 * createTime: 2021-03-22 17:39
 */
@Slf4j
@Configuration
public class NettyConfig implements InitializingBean {

    @Value("${zk.node.server}")
    private String zkServerNode;
    @Value("${im.server.port}")
    private Integer imServerPort;

    @Autowired
    private CuratorFramework client;

    @Override
    public void afterPropertiesSet() throws Exception {
        ServerBootstrap serverBootstrap = new ServerBootstrap()
                .group(new NioEventLoopGroup(1, new DefaultThreadFactory("boss")),
                        new NioEventLoopGroup(0, new DefaultThreadFactory("work-server")))
                .channel(NioServerSocketChannel.class)
                .childHandler(new ImServerInitializer());
        ChannelFuture future = serverBootstrap.bind(imServerPort).sync();
        if (future.isSuccess()) {
            log.info("服务端启动成功");
            registerServerIpToZk();
        } else {
            log.error("服务端启动失败", future.cause());
        }
    }

    private void registerServerIpToZk() {
        try {
            String hostAddress = InetAddress.getLocalHost().getHostAddress();
            client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL)
                    .forPath(String.format("%s/%s:%s", zkServerNode, hostAddress, imServerPort));
        } catch (UnknownHostException e) {
            log.error("获取本机ip地址出错", e);
        } catch (Exception e) {
            log.error("服务端注册到zk出错", e);
        }
    }
}
