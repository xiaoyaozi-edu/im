package com.xiaoyaozi.client;

import com.xiaoyaozi.protocol.ImMessageProto;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * tip:
 *
 * @author xiaoyaozi
 * createTime: 2021-03-21 21:48
 */
public class ImClientHandle extends SimpleChannelInboundHandler<ImMessageProto.ImMessage> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ImMessageProto.ImMessage message) throws Exception {
        System.out.println(message.getMsg());
    }
}
