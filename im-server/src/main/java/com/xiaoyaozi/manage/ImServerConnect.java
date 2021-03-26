package com.xiaoyaozi.manage;

import com.xiaoyaozi.config.ImServerConfig;
import com.xiaoyaozi.enums.ImMessageType;
import com.xiaoyaozi.handle.ImClientInitializer;
import com.xiaoyaozi.protocol.ImMessageProto;
import com.xiaoyaozi.server.MetaspaceConstant;
import com.xiaoyaozi.util.SpringUtil;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.DefaultThreadFactory;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * tip: S-S 互相连接
 *
 * @author xiaoyaozi
 * createTime: 2021-03-25 16:02
 */
@Slf4j
public class ImServerConnect {

    private static final Map<String, Integer> RECONNECT_COUNT_MAP = new ConcurrentHashMap<>(8);

    private static final ImServerConfig SERVER_CONFIG = SpringUtil.getBean(ImServerConfig.class);
    /**
     * tip: 尝试连接其他服务器
     *
     * @param serverIp serverIp
     * @author xiaoyaozi
     * createTime: 2021-03-23 23:37
     */
    public static void tryConnectOtherImServer(String serverIp) {
        Bootstrap bootstrap = new Bootstrap()
                .group(new NioEventLoopGroup(1, new DefaultThreadFactory("work-connect-server")))
                .channel(NioSocketChannel.class)
                .handler(new ImClientInitializer());
        String[] ipInfo = serverIp.split(":");
        try {
            ChannelFuture future = bootstrap.connect(ipInfo[0], Integer.parseInt(ipInfo[1])).sync();
            if (future.isSuccess()) {
                log.info("server连接server成功，其中ip是 {}", serverIp);
                // 取消重试任务
                ImServerReconnectManage.cancelReconnectTask(serverIp);
                // 次数清零
                RECONNECT_COUNT_MAP.remove(serverIp);
                Channel channel = future.channel();
                ImServerChannelManage.SERVER_CHANNEL_MAP.put(serverIp, (NioSocketChannel) channel);
                sendConnectSuccessInfo(channel);
            }
        } catch (InterruptedException e) {
            log.error("server连接server出错", e);
            Integer reconnectCount = RECONNECT_COUNT_MAP.get(serverIp);
            if (reconnectCount == null) {
                RECONNECT_COUNT_MAP.put(serverIp, 1);
            } else {
                RECONNECT_COUNT_MAP.put(serverIp, ++reconnectCount);
                if (reconnectCount >= SERVER_CONFIG.getReconnectMaxCount()) {
                    // 重连次数超标
                    log.error("server连接server失败次数：{}，超过上限，目标ip: {}", reconnectCount, serverIp);
                    ImServerReconnectManage.cancelReconnectTask(serverIp);
                }
            }

        }
    }

    /**
     * tip: 发送连接成功的信息至服务端
     *
     * @author xiaoyaozi
     * createTime: 2021-03-23 22:26
     */
    private static void sendConnectSuccessInfo(Channel channel) {
        // 注意：这里是发送自己的ip到服务端
        ImMessageProto.ImMessage message = ImMessageProto.ImMessage.newBuilder().setFromId(MetaspaceConstant.HOST_PORT_ADDRESS)
                .setType(ImMessageType.SERVER_CONNECT.getType()).build();
        channel.writeAndFlush(message);
    }
}
