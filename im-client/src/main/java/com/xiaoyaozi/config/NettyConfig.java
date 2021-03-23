package com.xiaoyaozi.config;

import cn.hutool.http.HttpRequest;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xiaoyaozi.base.R;
import com.xiaoyaozi.client.ImClientInitializer;
import com.xiaoyaozi.enums.ImMessageType;
import com.xiaoyaozi.protocol.ImMessageProto;
import com.xiaoyaozi.vo.LoginInfoReq;
import com.xiaoyaozi.vo.LoginInfoResp;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.DefaultThreadFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.util.Scanner;

/**
 * tip: 客户端Netty
 *
 * @author xiaoyaozi
 * createTime: 2021-03-23 16:18
 */
@Slf4j
@Configuration
public class NettyConfig implements InitializingBean {

    @Value("${user.account}")
    private String account;
    @Value("${user.password}")
    private String password;
    private Channel channel;

    @Override
    public void afterPropertiesSet() {
        prepareConnectImServer();
    }

    /**
     * tip: 准备连接服务器
     *
     * @author xiaoyaozi
     * createTime: 2021-03-23 23:38
     */
    @SuppressWarnings("unchecked")
    public void prepareConnectImServer() {
        String resp = HttpRequest.post("http://localhost:8899/login")
                .body(JSON.toJSONString(LoginInfoReq.builder().account(account).password(password).build()))
                .execute().body();
        R<JSONObject> parse = JSON.parseObject(resp, R.class);
        if (parse.isSuccess()) {
            tryConnectImServer(parse.getData().toJavaObject(LoginInfoResp.class));
        } else {
            log.error("登录接口异常，错误原因：{}", parse.getMsg());
        }
    }

    /**
     * tip: 尝试连接服务器
     *
     * @param loginInfo 用户信息
     * @author xiaoyaozi
     * createTime: 2021-03-23 23:37
     */
    private void tryConnectImServer(LoginInfoResp loginInfo) {
        Bootstrap bootstrap = new Bootstrap()
                .group(new NioEventLoopGroup(0, new DefaultThreadFactory("work-client")))
                .channel(NioSocketChannel.class)
                .handler(new ImClientInitializer());
        String[] ipInfo = loginInfo.getServerIp().split(":");
        ChannelFuture future;
        try {
            future = bootstrap.connect(ipInfo[0], Integer.parseInt(ipInfo[1])).sync();
            if (future.isSuccess()) {
                log.info("客户端启动成功");
                channel = future.channel();
                sendConnectSuccessInfo(loginInfo.getUserId());

                Scanner sc = new Scanner(System.in);
                while (true) {
                    String msg = sc.nextLine();
                    ImMessageProto.ImMessage messageProto = ImMessageProto.ImMessage.newBuilder().setFromId(1L).setType(ImMessageType.MESSAGE.getType()).setMsg(msg).build();
                    channel.writeAndFlush(messageProto);
                }
            }
        } catch (InterruptedException e) {
            log.error("client连接server出错", e);
        }
    }

    /**
     * tip: 发送连接成功的信息至服务端
     *
     * @author xiaoyaozi
     * createTime: 2021-03-23 22:26
     */
    private void sendConnectSuccessInfo(Long userId) {
        ImMessageProto.ImMessage message = ImMessageProto.ImMessage.newBuilder().setFromId(userId).setType(ImMessageType.CONNECT.getType()).build();
        channel.writeAndFlush(message);
    }
}
