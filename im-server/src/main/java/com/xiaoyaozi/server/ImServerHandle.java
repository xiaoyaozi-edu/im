package com.xiaoyaozi.server;

import com.xiaoyaozi.protocol.ImMessageProto;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * tip:
 *
 * @author xiaoyaozi
 * createTime: 2021-03-20 23:06
 */
public class ImServerHandle extends SimpleChannelInboundHandler<ImMessageProto.ImMessage> {

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, ImMessageProto.ImMessage imMessage) throws Exception {

    }

    /**
     * 用户端心跳丢失
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
    }
}
