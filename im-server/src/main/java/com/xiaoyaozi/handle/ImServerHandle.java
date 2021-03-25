package com.xiaoyaozi.handle;

import com.xiaoyaozi.enums.ImMessageType;
import com.xiaoyaozi.manage.ImServerChannelManage;
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
 * createTime: 2021-03-20 23:06
 */
@Slf4j
public class ImServerHandle extends SimpleChannelInboundHandler<ImMessageProto.ImMessage> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ImMessageProto.ImMessage message) throws Exception {
        if (message.getType() == ImMessageType.CLIENT_CONNECT.getType()) {
            log.info("客户端用户id：{}，成功连接服务器", message.getFromId());
            ImServerChannelManage.disposeClientConnectMessage(message.getFromId(), (NioSocketChannel) ctx.channel());
        } else if (message.getType() == ImMessageType.SERVER_CONNECT.getType()) {
            log.info("服务端ip地址：{}，成功连接服务器", message.getFromId());
            ImServerChannelManage.disposeServerConnectMessage(message.getFromId(), (NioSocketChannel) ctx.channel());
        } else if (message.getType() == ImMessageType.PING.getType()) {
            log.info("收到客户端心跳，消息来源：{}", message.getFromId());
            ImServerChannelManage.disposePingMessage((NioSocketChannel) ctx.channel());
        } else if (message.getType() == ImMessageType.MESSAGE.getType()) {
            log.info("收到客户端新消息，消息来源：{}，消息内容是：{}", message.getFromId(), message.getMsg());
            ImServerChannelManage.disposeNormalMessage((NioSocketChannel) ctx.channel(), message);
        } else {
            log.warn("收到未知类型的消息，type: {}, fromId: {}, message: {}", message.getType(), message.getFromId(), message.getMsg());
        } 
    }

    /**
     * channel失活
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        ImServerChannelManage.disposeChannelInactive((NioSocketChannel) ctx.channel());
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (ctx instanceof IdleStateEvent) {
            IdleState state = ((IdleStateEvent) ctx).state();
            if (state == IdleState.READER_IDLE) {
                ImServerChannelManage.disposePingTimeoutEvent((NioSocketChannel) ctx.channel());
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }
}
