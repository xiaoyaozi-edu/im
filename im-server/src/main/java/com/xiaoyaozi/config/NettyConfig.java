package com.xiaoyaozi.config;

import com.xiaoyaozi.server.ImServerInitializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.DefaultThreadFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * tip:
 *
 * @author xiaoyaozi
 * createTime: 2021-03-22 17:39
 */
@Slf4j
@Configuration
public class NettyConfig implements InitializingBean {

    @Value("${zk.path.server}")
    private String zkServerPath;
    @Value("${im.server.port")
    private String imServerPort;

    @Autowired
    private CuratorFramework client;

    @Override
    public void afterPropertiesSet() throws Exception {
        ServerBootstrap serverBootstrap = new ServerBootstrap()
                .group(new NioEventLoopGroup(1, new DefaultThreadFactory("boss")),
                        new NioEventLoopGroup(0, new DefaultThreadFactory("work-server")))
                .channel(NioServerSocketChannel.class)
                .localAddress(10010)
                .childHandler(new ImServerInitializer());
        ChannelFuture future = serverBootstrap.bind(10010).sync();
        if (future.isSuccess()) {
            log.info("服务端启动成功");
//            client.create().creatingParentsIfNeeded().forPath(zkServerPath)
        } else {
            log.error("服务端启动失败", future.cause());
        }
    }

    private void registerServerIpToZk() {
        
    }
}
