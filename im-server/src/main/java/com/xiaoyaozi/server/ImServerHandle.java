package com.xiaoyaozi.server;

import com.xiaoyaozi.enums.ImMessageType;
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
    protected void channelRead0(ChannelHandlerContext ctx, ImMessageProto.ImMessage imMessage) throws Exception {
        System.out.println(imMessage.getMsg());
        ImMessageProto.ImMessage messageProto = ImMessageProto.ImMessage.newBuilder().setFromId(2L).setType(ImMessageType.MESSAGE.getType()).setMsg("服务端返回消息：" + imMessage.getMsg()).build();
        ctx.writeAndFlush(messageProto);
    }

    /**
     * 用户端心跳丢失
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
    }
}
