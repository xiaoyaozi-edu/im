package com.xiaoyaozi.util;

import com.xiaoyaozi.enums.ImMessageType;
import com.xiaoyaozi.protocol.ImMessageProto;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * tip: im心跳工具
 *
 * @author xiaoyaozi
 * createTime: 2021-03-26 14:13
 */
public class ImPingUtil {

    private static final ImMessageProto.ImMessage PING_MESSAGE = ImMessageProto.ImMessage.newBuilder().setFromId("0")
            .setType(ImMessageType.PING.getType()).build();

    /**
     * tip: 发送心跳包
     *
     * @param channel channel
     * @author xiaoyaozi
     * createTime: 2021-03-24 17:54
     */
    public static void sendPingMessage(NioSocketChannel channel) {
        channel.writeAndFlush(PING_MESSAGE);
    }
}
