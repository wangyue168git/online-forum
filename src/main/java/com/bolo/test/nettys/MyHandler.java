package com.bolo.test.nettys;

import com.sun.org.apache.xpath.internal.operations.String;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.apache.log4j.Logger;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.util.Arrays;

/**
 * @Author wangyue
 * @Date 19:58
 */
public class MyHandler extends ChannelInboundHandlerAdapter {

    private static final Logger logger = Logger.getLogger(MyHandler.class.getName());

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ctx.write("123123");
        System.out.println("start..." + ctx.read());
        ByteBuf byteBuf = (ByteBuf) msg;
        byte[] s = new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(s);
        System.out.println(s);

        ByteArrayInputStream is = new ByteArrayInputStream(s);

    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
        System.out.println("end...");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        // Close the connection when an exception is raised.

        ctx.close();
    }
}
