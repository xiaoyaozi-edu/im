package com.xiaoyaozi.manage;

import com.xiaoyaozi.config.NettyConfig;
import com.xiaoyaozi.constant.RedisConstant;
import com.xiaoyaozi.enums.ImMessageType;
import com.xiaoyaozi.handle.ImClientInitializer;
import com.xiaoyaozi.protocol.ImMessageProto;
import com.xiaoyaozi.util.RedisUtil;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.DefaultThreadFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * tip: 管理 C-S 间的channel
 *
 * @author xiaoyaozi
 * createTime: 2021-03-24 09:42
 */
@Slf4j
@Component
public class ImServerChannelManage {

    /**
     * 保存所有 C-S 的socket连接
     */
    private static final Map<String, NioSocketChannel> CLIENT_CHANNEL_MAP = new ConcurrentHashMap<>(16);
    /**
     * 保存所有 S-S 的socket连接
     */
    public static final Map<String, NioSocketChannel> SERVER_CHANNEL_MAP = new ConcurrentHashMap<>(16);
    /**
     * socket上次心跳时间
     */
    private static final AttributeKey<String> RECENTLY_PING_TIME = AttributeKey.valueOf("recentlyPingTime");
    /**
     * channel绑定的userId信息
     */
    private static final AttributeKey<String> USERID_INFO = AttributeKey.valueOf("userId");
    /**
     * 心跳最长超时
     */
    public static Integer PING_TIMEOUT;

    /**
     * tip: 处理 C-S 信息
     *
     * @param userId 用户id
     * @param channel channel
     * @author xiaoyaozi
     * createTime: 2021-03-24 11:46
     */
    public static void disposeClientConnectMessage(String userId, NioSocketChannel channel) {
        updateRecentlyPingTime(channel);
        channel.attr(USERID_INFO).set(userId);
        CLIENT_CHANNEL_MAP.put(userId, channel);
        RedisUtil.set(RedisConstant.ROUTE_PREFIX + userId, NettyConfig.HOST_PORT_ADDRESS);
    }

    /**
     * tip: 处理 S-S 信息
     *
     * @param serverIp serverIp
     * @param channel channel
     * @author xiaoyaozi
     * createTime: 2021-03-25 16:32
     */
    public static void disposeServerConnectMessage(String serverIp, NioSocketChannel channel) {
        updateRecentlyPingTime(channel);
        channel.attr(USERID_INFO).set(serverIp);
        SERVER_CHANNEL_MAP.put(serverIp, channel);
    }

    /**
     * tip: 处理心跳信息
     *
     * @param channel channel
     * @author xiaoyaozi
     * createTime: 2021-03-24 14:10
     */
    public static void disposePingMessage(NioSocketChannel channel) {
        updateRecentlyPingTime(channel);
    }

    /**
     * tip: 处理普通聊天消息
     *
     * @param channel channel
     * @param imMessage 消息体
     * @author xiaoyaozi
     * createTime: 2021-03-24 16:26
     */
    public static void disposeNormalMessage(NioSocketChannel channel, ImMessageProto.ImMessage imMessage) {
        String toId = imMessage.getToId();
        if (StringUtils.isEmpty(toId)) {
            log.error("客户端消息toId丢失，请检查，其中fromId: {}", imMessage.getFromId());
            return;
        }
        // 先查询本地
        NioSocketChannel toIdChannel = CLIENT_CHANNEL_MAP.get(toId);
        if (toIdChannel != null) {
            toIdChannel.writeAndFlush(imMessage);
        } else {
            // 再查询缓存中路由信息
            String serverIp = RedisUtil.get(RedisConstant.ROUTE_PREFIX + toId);
            if (StringUtils.isEmpty(serverIp)) {
                // 缓存不存在，用户未上线
                channel.writeAndFlush(ImMessageProto.ImMessage.newBuilder().setType(ImMessageType.MESSAGE.getType())
                        .setFromId("0").setMsg("用户未上线，延迟推送").build());
            } else {
                // 缓存存在，查询本地serverMap
                NioSocketChannel toServerIpChannel = CLIENT_CHANNEL_MAP.get(serverIp);
                if (toServerIpChannel != null) {
                    toServerIpChannel.writeAndFlush(imMessage);
                } else {
                    // 本地不存在server，延迟推送
                    channel.writeAndFlush(ImMessageProto.ImMessage.newBuilder().setType(ImMessageType.MESSAGE.getType())
                            .setFromId("0").setMsg("用户未上线，延迟推送").build());
                }
            }
        }
    }

    /**
     * tip: 处理心跳超时事件
     *
     * @param channel channel
     * @author xiaoyaozi
     * createTime: 2021-03-24 14:45
     */
    public static void disposePingTimeoutEvent(NioSocketChannel channel) {
        long recentlyPingTime = Long.parseLong(channel.attr(RECENTLY_PING_TIME).get());
        if (System.currentTimeMillis() - recentlyPingTime > PING_TIMEOUT) {
            log.warn("客户端心跳超时，即将关闭连接，用户id: {}", channel.attr(USERID_INFO).get());
            clientOffline(channel);
        }
    }

    /**
     * tip: 处理channel失活
     *
     * @param channel channel
     * @author xiaoyaozi
     * createTime: 2021-03-24 14:21
     */
    public static void disposeChannelInactive(NioSocketChannel channel) {
        clientOffline(channel);
    }

    /**
     * tip: 客户端下线
     *
     * @param channel channel
     * @author xiaoyaozi
     * createTime: 2021-03-24 14:40
     */
    private static void clientOffline(NioSocketChannel channel) {
        String userId = channel.attr(USERID_INFO).get();
        // 这里userId偶现null，加一个判断
        if (StringUtils.isNotEmpty(userId)) {
            CLIENT_CHANNEL_MAP.remove(userId);
            SERVER_CHANNEL_MAP.remove(userId);
            RedisUtil.delete(RedisConstant.ROUTE_PREFIX + userId);
            ChannelFuture future = channel.close();
            if (future.isSuccess()) {
                log.info("客户端用户id：{}，成功下线服务器", userId);
            } else {
                log.error("客户端用户id：{}，下线服务器时，关闭channel出现错误", userId, future.cause());
            }
        }
    }

    /**
     * tip: 更新上次心跳时间
     *
     * @param channel channel
     * @author xiaoyaozi
     * createTime: 2021-03-24 15:59
     */
    private static void updateRecentlyPingTime(NioSocketChannel channel) {
        channel.attr(RECENTLY_PING_TIME).set(String.valueOf(System.currentTimeMillis()));
    }

    /**
     * tip: 更新服务端之间的socket连接
     *
     * @param serverIpList 服务端ip地址
     * @author xiaoyaozi
     * createTime: 2021-03-25 10:16
     */
    public static void updateServerSocketConnect(List<String> serverIpList) {
        for (String serverIp : serverIpList) {
            if (!serverIp.equals(NettyConfig.HOST_PORT_ADDRESS) && !SERVER_CHANNEL_MAP.containsKey(serverIp) ) {
                // 创建连接--这里使用任务的方式进行连接
                ImServerReconnectManage.appendReconnectTask(serverIp);
            }
        }
    }
}

