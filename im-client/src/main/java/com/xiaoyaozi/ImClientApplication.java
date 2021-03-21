package com.xiaoyaozi;

import com.xiaoyaozi.client.ImClientInitializer;
import com.xiaoyaozi.constant.ImMessageConstant;
import com.xiaoyaozi.enums.ImMessageType;
import com.xiaoyaozi.protocol.ImMessageProto;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.DefaultThreadFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Scanner;

/**
 * tip: 客户端启动
 *
 * @author xiaoyaozi
 * createTime: 2021-03-20 23:38
 */
@Slf4j
@SpringBootApplication
public class ImClientApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(ImClientApplication.class);
    }

    public void run(String... args) throws Exception {
        Bootstrap bootstrap = new Bootstrap()
                .group(new NioEventLoopGroup(0, new DefaultThreadFactory("work-client")))
                .channel(NioSocketChannel.class)
                .handler(new ImClientInitializer());
        ChannelFuture future = bootstrap.connect("127.0.0.1",10010).sync();
        if (future.isSuccess()) {
            log.info("客户端启动成功");
        }
        Channel channel = future.channel();
        Scanner sc = new Scanner(System.in);
        while (true) {
            String msg = sc.nextLine();
            ImMessageProto.ImMessage messageProto = ImMessageProto.ImMessage.newBuilder().setFromId(1L).setType(ImMessageType.MESSAGE.getType()).setMsg(msg).build();
            channel.writeAndFlush(messageProto);
        }
    }
}
