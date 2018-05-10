package com.bolo.crawler;

import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.protocol.HttpContext;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * @Author wangyue
 * @Date 15:14
 */
public class DefaultConnectionSocketFactory extends PlainConnectionSocketFactory {

    public static final String SOCKS_ADDRESS = "socks.address";
    public static final String SOCKS_PROXY = "socksProxy";

    public static final DefaultConnectionSocketFactory INSTANCE = new DefaultConnectionSocketFactory();

    public static DefaultConnectionSocketFactory getSocketFactory() {
        return INSTANCE;
    }

    public DefaultConnectionSocketFactory() {
    }

    public Socket createSocket(final HttpContext context) throws IOException {
        String socksProxy = (String) context.getAttribute(SOCKS_PROXY);
        if ("true".equalsIgnoreCase(socksProxy)) {
            InetSocketAddress socksAddress = (InetSocketAddress) context.getAttribute(SOCKS_ADDRESS);
            java.net.Proxy proxy = new java.net.Proxy(java.net.Proxy.Type.SOCKS, socksAddress);
            return new Socket(proxy);
        }
        return new Socket();
    }
}
