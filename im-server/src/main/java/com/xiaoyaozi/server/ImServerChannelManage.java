package com.xiaoyaozi.server;

import com.xiaoyaozi.config.NettyConfig;
import com.xiaoyaozi.constant.RedisConstant;
import com.xiaoyaozi.enums.ImMessageType;
import com.xiaoyaozi.protocol.ImMessageProto;
import com.xiaoyaozi.util.RedisUtil;
import io.netty.channel.ChannelFuture;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * tip: 服务端channel管理类
 *
 * @author xiaoyaozi
 * createTime: 2021-03-24 09:42
 */
@Slf4j
@Component
public class ImServerChannelManage {

    /**
     * 保存所有socket连接
     */
    private static final Map<Long, NioSocketChannel> CHANNEL_MAP = new ConcurrentHashMap<>(16);
    /**
     * socket上次心跳时间
     */
    private static final AttributeKey<String> RECENTLY_PING_TIME = AttributeKey.valueOf("recentlyPingTime");
    /**
     * channel绑定的userId信息
     */
    private static final AttributeKey<String> USERID_INFO = AttributeKey.valueOf("userId");

    public static Integer PING_TIMEOUT;

    /**
     * tip: 处理连接信息
     *
     * @param userId 用户id
     * @param channel channel
     * @author xiaoyaozi
     * createTime: 2021-03-24 11:46
     */
    public static void disposeConnectMessage(Long userId, NioSocketChannel channel) {
        updateRecentlyPingTime(channel);
        channel.attr(USERID_INFO).set(userId.toString());
        CHANNEL_MAP.put(userId, channel);
        RedisUtil.set(RedisConstant.ROUTE_PREFIX + userId, NettyConfig.HOST_PORT_ADDRESS);
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
        long toId = imMessage.getToId();
        if (toId == 0) {
            log.error("客户端消息toId丢失，请检查，其中fromId: {}", imMessage.getFromId());
            return;
        }
        NioSocketChannel toIdChannel = CHANNEL_MAP.get(toId);
        if (toIdChannel != null) {
            toIdChannel.writeAndFlush(imMessage);
        } else {
            // 本地不存在，先查询缓存中路由信息
            if (RedisUtil.isExist(RedisConstant.ROUTE_PREFIX + toId)) {

            } else {
                // 缓存不存在，用户未上线
                channel.writeAndFlush(ImMessageProto.ImMessage.newBuilder().setType(ImMessageType.MESSAGE.getType())
                        .setFromId(-1).setMsg("用户未上线，发送失败").build());
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
        Long recentlyPingTime = Long.valueOf(channel.attr(RECENTLY_PING_TIME).get());
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
        CHANNEL_MAP.remove(Long.valueOf(userId));
        RedisUtil.delete(RedisConstant.ROUTE_PREFIX + userId);
        ChannelFuture future = channel.close();
        if (future.isSuccess()) {
            log.info("客户端用户id：{}，成功下线服务器", userId);
        } else {
            log.error("客户端用户id：{}，下线服务器时，关闭channel出现错误", userId, future.cause());
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
}

