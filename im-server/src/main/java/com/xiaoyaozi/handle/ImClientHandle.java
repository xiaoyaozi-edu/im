package com.xiaoyaozi.handle;

import com.xiaoyaozi.enums.ImMessageType;
import com.xiaoyaozi.manage.ImServerChannelManage;
import com.xiaoyaozi.manage.ImServerReconnectManage;
import com.xiaoyaozi.protocol.ImMessageProto;
import com.xiaoyaozi.server.MetaspaceConstant;
import com.xiaoyaozi.util.ImPingUtil;
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
        log.info("{}", message.getType());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        ImServerReconnectManage.appendReconnectTask(ctx.channel().attr(ImServerChannelManage.USERID_INFO).get());
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
                log.info("[s-s] 即将发送心跳，其中目标serverIp: {}", MetaspaceConstant.HOST_PORT_ADDRESS);
                ImPingUtil.sendPingMessage((NioSocketChannel) ctx.channel());
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }
}
