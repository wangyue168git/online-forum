package com.bolo.test.nettys.server;


import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.apache.log4j.Logger;

/**
 * @Author wangyue
 * @Date 19:58
 */
public class MyHandler extends ChannelInboundHandlerAdapter {

    private static final Logger logger = Logger.getLogger(MyHandler.class.getName());
    private ByteBuf firstMessage  = Unpooled.buffer(33);

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {


//        ctx.write("123123");
//        System.out.println("start..." + ctx.read());


        ByteBuf byteBuf = (ByteBuf) msg;
        byte[] s = new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(s);
        System.out.println(new String(s));

        firstMessage.writeBytes("Hi...".getBytes());
        ctx.writeAndFlush(firstMessage);

    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {

        System.out.print("active");
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {

        System.out.println("end...");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        // Close the connection when an exception is raised.

        ctx.close();
    }
}
