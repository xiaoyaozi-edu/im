package com.xiaoyaozi;

import com.xiaoyaozi.server.ImServerInitializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.DefaultThreadFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * tip: 服务端启动
 *
 * @author xiaoyaozi
 * createTime: 2021-03-19 17:36
 */
@SpringBootApplication
public class ImServerApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(ImServerApplication.class);
    }

    public void run(String... args) throws Exception {
        new ServerBootstrap()
                .group(new NioEventLoopGroup(1, new DefaultThreadFactory("boss")),
                        new NioEventLoopGroup(0, new DefaultThreadFactory("work-server")))
                .channel(NioServerSocketChannel.class)
                .localAddress(10010)
                .childHandler(new ImServerInitializer());
        Thread.currentThread().join();
    }
}
