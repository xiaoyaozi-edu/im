package com.xiaoyaozi.client;

import com.xiaoyaozi.protocol.ImMessageProto;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * tip:
 *
 * @author xiaoyaozi
 * createTime: 2021-03-24 17:41
 */
public class ImClientChannelManage {

    public static ImMessageProto.ImMessage PING_MESSAGE;

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
