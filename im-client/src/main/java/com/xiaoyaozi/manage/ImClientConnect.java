package com.xiaoyaozi.manage;

import cn.hutool.http.HttpRequest;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xiaoyaozi.base.R;
import com.xiaoyaozi.client.ImClientInitializer;
import com.xiaoyaozi.config.ImClientConfig;
import com.xiaoyaozi.enums.ImMessageType;
import com.xiaoyaozi.protocol.ImMessageProto;
import com.xiaoyaozi.util.ImMessageIdUtil;
import com.xiaoyaozi.util.SpringUtil;
import com.xiaoyaozi.vo.LoginInfoReq;
import com.xiaoyaozi.vo.LoginInfoResp;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.DefaultThreadFactory;
import lombok.extern.slf4j.Slf4j;

import java.util.Scanner;

/**
 * tip: 客户端连接
 *
 * @author xiaoyaozi
 * createTime: 2021-03-25 16:02
 */
@Slf4j
public class ImClientConnect {

    private static final ImClientConfig CLIENT_CONFIG = SpringUtil.getBean(ImClientConfig.class);

    /**
     * client重连次数
     */
    private static int reconnectCount;

    private static Channel channel;


    /**
     * tip: 准备连接服务器
     *
     * @author xiaoyaozi
     * createTime: 2021-03-23 23:38
     */
    @SuppressWarnings("unchecked")
    public static void prepareConnectImServer() {
        try {
            String resp = HttpRequest.post("http://localhost:8899/login")
                    .body(JSON.toJSONString(LoginInfoReq.builder().account(CLIENT_CONFIG.getAccount()).password(CLIENT_CONFIG.getPassword()).build()))
                    .execute().body();
            R<JSONObject> parse = JSON.parseObject(resp, R.class);
            if (parse.isSuccess()) {
                tryConnectImServer(parse.getData().toJavaObject(LoginInfoResp.class));
            } else {
                log.error("登录接口异常，错误原因：{}", parse.getMsg());
                reconnectCount++;
            }
        } catch (Exception e) {
            log.error("登录接口异常", e);
            reconnectCount++;
        }
        if (reconnectCount >= CLIENT_CONFIG.getReconnectMaxCount()) {
            // 重连次数超标
            log.error("client连接server失败次数：{}，超过上限，client即将退出", reconnectCount);
            prepateShutdownClient();
        }
    }

    /**
     * tip: 尝试连接服务器
     *
     * @param loginInfo 用户信息
     * @author xiaoyaozi
     * createTime: 2021-03-23 23:37
     */
    private static void tryConnectImServer(LoginInfoResp loginInfo) {
        Bootstrap bootstrap = new Bootstrap()
                .group(new NioEventLoopGroup(0, new DefaultThreadFactory("work-client")))
                .channel(NioSocketChannel.class)
                .handler(new ImClientInitializer());
        String[] ipInfo = loginInfo.getServerIp().split(":");
        try {
            ChannelFuture future = bootstrap.connect(ipInfo[0], Integer.parseInt(ipInfo[1])).sync();
            if (future.isSuccess()) {
                log.info("客户端启动成功");
                channel = future.channel();
                Long userId = loginInfo.getUserId();
                sendConnectSuccessInfo(userId);
                // 次数清零
                reconnectCount = 0;
                // 取消重试任务
                ImClientReconnectManage.cancelReconnectTask();

                Scanner sc = new Scanner(System.in);
                while (true) {
                    String msg = sc.nextLine();
                    String[] split = msg.split(";");
                    ImMessageProto.ImMessage messageProto = ImMessageProto.ImMessage.newBuilder().setType(ImMessageType.NORMAL_MESSAGE.getType())
                            .setFromId(userId.toString()).setToId(split[0]).setMessageId(ImMessageIdUtil.generate(userId)).setMsg(split[1]).build();
                    channel.writeAndFlush(messageProto);

                    // todo 这里需要做客户端做出消息重复处理，留着各个客户端进行处理，在这里将消息加入到重试队列中
                }
            }
        } catch (InterruptedException e) {
            log.error("client连接server出错", e);
            reconnectCount++;
        }
    }

    /**
     * tip: 准备关闭客户端
     *
     * @author xiaoyaozi
     * createTime: 2021-03-25 16:06
     */
    private static void prepateShutdownClient() {
        System.exit(0);
    }

    /**
     * tip: 发送连接成功的信息至服务端
     *
     * @author xiaoyaozi
     * createTime: 2021-03-23 22:26
     */
    private static void sendConnectSuccessInfo(Long userId) {
        ImMessageProto.ImMessage message = ImMessageProto.ImMessage.newBuilder().setFromId(userId.toString()).setType(ImMessageType.CLIENT_CONNECT.getType()).build();
        channel.writeAndFlush(message);
    }
}
