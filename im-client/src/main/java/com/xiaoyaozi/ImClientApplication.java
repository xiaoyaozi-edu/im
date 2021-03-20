package com.xiaoyaozi;

import com.xiaoyaozi.client.ImClientInitializer;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.DefaultThreadFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * tip: 客户端启动
 *
 * @author xiaoyaozi
 * createTime: 2021-03-20 23:38
 */
@SpringBootApplication
public class ImClientApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(ImClientApplication.class);
    }

    public void run(String... args) throws Exception {
        new Bootstrap()
                .group(new NioEventLoopGroup(0, new DefaultThreadFactory("work-client")))
                .channel(NioSocketChannel.class)
                .handler(new ImClientInitializer());
        Thread.currentThread().join();
    }
}
