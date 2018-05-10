package com.bolo.crawler;

import org.apache.http.HttpHost;
import org.apache.http.conn.ssl.AllowAllHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.protocol.HttpContext;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * @Author wangyue
 * @Date 15:12
 */
public class DefaultSSLConnectionSocketFactory  extends SSLConnectionSocketFactory{
    public static final String SOCKS_ADDRESS = "socks.address";
    public static final String SOCKS_PROXY = "socksProxy";

    public DefaultSSLConnectionSocketFactory(final SSLContext sslContext, AllowAllHostnameVerifier allowAllHostnameVerifier) {
        // You may need this verifier if target site's certificate is not secure
        super(sslContext, allowAllHostnameVerifier);
    }

    @Override
    public Socket createSocket(final HttpContext context) throws IOException {
        String socksProxy = (String) context.getAttribute(SOCKS_PROXY);
        if ("true".equalsIgnoreCase(socksProxy)) {
            InetSocketAddress socksAddress = (InetSocketAddress) context.getAttribute(SOCKS_ADDRESS);
            java.net.Proxy proxy = new java.net.Proxy(java.net.Proxy.Type.SOCKS, socksAddress);
            return new Socket(proxy);
        }
        return new Socket();
    }

    @Override
    public Socket connectSocket(int connectTimeout, Socket socket, HttpHost host, InetSocketAddress remoteAddress,
                                InetSocketAddress localAddress, HttpContext context) throws IOException {
        // Convert address to unresolved
        String socksProxy = (String) context.getAttribute(SOCKS_PROXY);
        if ("true".equalsIgnoreCase(socksProxy)) {
            InetSocketAddress unresolvedRemote = InetSocketAddress
                    .createUnresolved(host.getHostName(), remoteAddress.getPort());
            return super.connectSocket(connectTimeout, socket, host, unresolvedRemote, localAddress, context);
        } else {
            return super.connectSocket(connectTimeout, socket, host, remoteAddress, localAddress, context);
        }
    }
}
