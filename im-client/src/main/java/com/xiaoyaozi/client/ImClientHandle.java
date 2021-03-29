package com.xiaoyaozi.client;

import com.xiaoyaozi.enums.ImMessageType;
import com.xiaoyaozi.manage.ImClientReconnectManage;
import com.xiaoyaozi.protocol.ImMessageProto;
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
        if (message.getType() == ImMessageType.NORMAL_MESSAGE.getType()) {
            log.info("收到新消息，消息来源：{}，消息内容是：{}", message.getFromId(), message.getMsg());
        } else if (message.getType() == ImMessageType.SERVER_SUCCESS_RECEIVE.getType()) {
            log.info("messageId：{}发送成功", message.getMessageId());

            // todo 这里需要做客户端做出消息重复处理，留着各个客户端进行处理，在这里将消息从重试队列中移除
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
                log.info("[c-s] 即将发送心跳");
                ImPingUtil.sendPingMessage((NioSocketChannel) ctx.channel());
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }
}
