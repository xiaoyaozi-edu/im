package com.xiaoyaozi.client;

import com.xiaoyaozi.enums.ImMessageType;
import com.xiaoyaozi.manage.ImClientReconnectManage;
import com.xiaoyaozi.protocol.ImMessageProto;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;

/**
 * tip:
 *
 * @author xiaoyaozi
 * createTime: 2021-03-21 21:48
 */
@Slf4j
public class ImClientHandle extends SimpleChannelInboundHandler<ImMessageProto.ImMessage> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ImMessageProto.ImMessage message) throws Exception {
        if (message.getType() == ImMessageType.MESSAGE.getType()) {
            log.info("收到新消息，消息来源：{}，消息内容是：{}", message.getFromId(), message.getMsg());
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        ImClientReconnectManage.appendReconnectTask();
        ctx.channel().close();
    }

    /**
     * ping、pong会来到此回调
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleState state = ((IdleStateEvent) evt).state();
            if (state == IdleState.WRITER_IDLE) {
                log.info("client即将发送心跳");
                ImClientChannelManage.sendPingMessage((NioSocketChannel) ctx.channel());
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }
}
