package com.xiaoyaozi.server;

import com.xiaoyaozi.enums.ImMessageType;
import com.xiaoyaozi.protocol.ImMessageProto;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;

/**
 * tip:
 *
 * @author xiaoyaozi
 * createTime: 2021-03-20 23:06
 */
@Slf4j
public class ImServerHandle extends SimpleChannelInboundHandler<ImMessageProto.ImMessage> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ImMessageProto.ImMessage imMessage) throws Exception {
        if (imMessage.getType() == ImMessageType.CONNECT.getType()) {
            System.out.println(imMessage.getFromId());
            System.out.println(imMessage.getMsg());
        } else if (imMessage.getType() == ImMessageType.PING.getType()) {

        } else if (imMessage.getType() == ImMessageType.MESSAGE.getType()) {

        } else {
            log.warn("收到未知类型的消息，type: {}, fromId: {}, message: {}", imMessage.getType(), imMessage.getFromId(), imMessage.getMsg());
        } 
    }

    /**
     * 用户端心跳丢失
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (ctx instanceof IdleStateEvent) {
            IdleState state = ((IdleStateEvent) ctx).state();
            if (state == IdleState.READER_IDLE) {

            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }
}
