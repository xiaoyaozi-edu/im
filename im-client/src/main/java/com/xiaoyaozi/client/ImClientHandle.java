package com.xiaoyaozi.client;

import com.xiaoyaozi.protocol.ImMessageProto;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.concurrent.atomic.AtomicInteger;

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


    public static void main(String[] args) {
        System.out.println(Integer.MAX_VALUE);
        System.out.println(1L << 31);
        AtomicInteger index = new AtomicInteger(Integer.MAX_VALUE);
        int i = index.addAndGet(2);
        int i1 = i % 2;
        System.out.println(Math.abs(i1));
        System.out.println(i1);
        System.out.println(index.addAndGet(2));
    }
}
