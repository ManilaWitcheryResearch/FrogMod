package com.frogmcs.frogmod.mcs.http;

import com.frogmcs.frogmod.mcs.MessageHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * Created by swordfeng on 16-11-18.
 */
public class HttpServerHandler extends SimpleChannelInboundHandler<Object> {
    private MessageHandler mHandler;
    public HttpServerHandler(MessageHandler handler) {
        mHandler = handler;
    }
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {

    }
}
