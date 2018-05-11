package com.bolo.crawler.httpclient;

import com.bolo.crawler.entitys.Request;
import com.bolo.crawler.entitys.Site;
import com.bolo.crawler.utils.CookieStoreUtil;
import com.bolo.crawler.utils.SiteUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.*;
import org.apache.http.client.CookieStore;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.ssl.AllowAllHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.cookie.*;
import org.apache.http.impl.client.*;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.impl.cookie.*;
import org.apache.http.protocol.HttpContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.*;
import java.io.IOException;
import java.net.Socket;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

/**
 * @Author wangyue
 * @Date 15:10
 */
public class HttpClientGenerator {

    private Logger logger = LoggerFactory.getLogger("Generator");

    private PoolingHttpClientConnectionManager connectionManager;

    public HttpClientGenerator() {
        this(null);
    }
    public HttpClientGenerator(Site site) {
        init(site);
    }
    private void init(Site site) {
        //new X509Extended7DeployTrustManager();
        //X509ExtendedTrustManager
        //arg2.setEnabledProtocols(new String[] { "SSLv3" });
        X509TrustManager xtm = new X509ExtendedTrustManager(){   //创建TrustManager
            public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {}
            public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {}
            public X509Certificate[] getAcceptedIssuers() { return null; }
            public void checkClientTrusted(X509Certificate[] arg0, String arg1, Socket arg2) throws CertificateException {}
            public void checkClientTrusted(X509Certificate[] arg0, String arg1, SSLEngine arg2) throws CertificateException {}
            public void checkServerTrusted(X509Certificate[] arg0, String arg1, Socket arg2) throws CertificateException {}
            public void checkServerTrusted(X509Certificate[] arg0, String arg1, SSLEngine arg2) throws CertificateException {};
        };
        try {
            Registry<ConnectionSocketFactory> reg = null;
            String sslProtocol = site == null || StringUtils.isBlank(site.getSslProtocolContext()) ? SSLConnectionSocketFactory.TLS : site.getSslProtocolContext();
            SSLContext ctx = null;
            try {
                ctx = SSLContext.getInstance(sslProtocol);
            } catch (Exception e) {
                logger.info("sslProtocol:", e);
                ctx = SSLContext.getInstance(SSLConnectionSocketFactory.TLS);
            }
            try {
                ctx.init(null, new TrustManager[]{xtm}, new SecureRandom());
                //SSLv2Hello,SSLv3,TLSv1,TLSv1.1,TLSv1.2
                SSLContext.setDefault(ctx);
                //String[] protocols = new String[]{"SSLv3"};
                DefaultSSLConnectionSocketFactory sslSocketFactory = new DefaultSSLConnectionSocketFactory(ctx, new AllowAllHostnameVerifier());//, protocols, null, null);
                //sslSocketFactory.
                reg = RegistryBuilder.<ConnectionSocketFactory>create()
                        .register("http", DefaultConnectionSocketFactory.INSTANCE)
                        .register("https", sslSocketFactory)
                        //.register("https", SSLConnectionSocketFactory.getSocketFactory())
                        .build();
            } catch (Exception e) {
                logger.info("sslSocketFactory:", e);
            }
            connectionManager = new PoolingHttpClientConnectionManager(reg);
            connectionManager.setDefaultMaxPerRoute(100);
            connectionManager.setMaxTotal(1000);
        } catch (Exception e) {
            logger.info("connectionManager:", e);
        }
    }

    public HttpClientGenerator setPoolSize(int poolSize) {
        connectionManager.setMaxTotal(poolSize);
        return this;
    }

    public CloseableHttpClient getClient(Site site, Request request) {
        return generateClient(site, request);
    }

    private CloseableHttpClient generateClient(Site site, Request request) {
        HttpClientBuilder httpClientBuilder = HttpClients.custom().setConnectionManager(connectionManager);
        //ignoreSSLSocket1(site, httpClientBuilder);
        if (site != null && site.getUserAgent() != null) {
            httpClientBuilder.setUserAgent(site.getUserAgent());
        } else {
            httpClientBuilder.setUserAgent("");
        }
        if (site == null || site.isUseGzip()) {
            httpClientBuilder.addInterceptorFirst(new HttpRequestInterceptor() {
                public void process(
                        final HttpRequest request,
                        final HttpContext context) throws HttpException, IOException {
                    if (!request.containsHeader("Accept-Encoding")) {
                        request.addHeader("Accept-Encoding", "gzip");
                    }

                }
            });
        }
        SocketConfig socketConfig = SocketConfig.custom().setSoKeepAlive(true).setTcpNoDelay(true).build();
        httpClientBuilder.setDefaultSocketConfig(socketConfig);
        if (site != null) {
            httpClientBuilder.setRetryHandler(new DefaultHttpRequestRetryHandler(site.getRetryTimes(), true));
        }
        if (request != null && request.isUseProxy()) {
            HttpHost hh = (HttpHost) request.getExtra(Request.PROXY);
            if (hh != null) {
                String sno = (String) request.getExtra(Request.PROXY_SNO);
                logger.warn(sno != null ? sno : String.format("Proxy %s:%d", hh.getHostName(), hh.getPort()));

                //socket 时这里不能设置值
                if(site == null || !site.isSocketProxy()) {
                    httpClientBuilder.setProxy(hh); //设置代理
                }
            }
        }
        if (site.isFollowRedirect()) {
            httpClientBuilder.setRedirectStrategy(new CharacterRedirectStrategy(site.getReplaceCharsWhenRedirect()));
        }
        generateCookie(httpClientBuilder, site);
        if (StringUtils.isNotBlank(site.getCookieSpec())) {
            RequestConfig requestConfig = RequestConfig.custom()
                    .setCookieSpec(site.getCookieSpec()).build();
            httpClientBuilder.setDefaultRequestConfig(requestConfig);
            if ("mySpec".equalsIgnoreCase(site.getCookieSpec())) {
                CookieSpecProvider easySpecProvider = new CookieSpecProvider() {
                    public CookieSpec create(HttpContext context) {
                        return new BrowserCompatSpec() {
                            @Override
                            public void validate(Cookie cookie, CookieOrigin origin)
                                    throws MalformedCookieException {
                                // Oh, I am easy
                            }
                        };
                    }
                };
                Registry<CookieSpecProvider> reg = RegistryBuilder.<CookieSpecProvider>create()
                        .register(CookieSpecs.BEST_MATCH, new BestMatchSpecFactory())
                        .register(CookieSpecs.STANDARD, new RFC2965SpecFactory())
                        .register(CookieSpecs.BROWSER_COMPATIBILITY, new BrowserCompatSpecFactory())
                        .register(CookieSpecs.NETSCAPE, new NetscapeDraftSpecFactory())
                        .register(CookieSpecs.IGNORE_COOKIES, new IgnoreSpecFactory())
                        .register("rfc2109", new RFC2109SpecFactory())
                        .register("rfc2965", new RFC2965SpecFactory())
                        .register("mySpec", easySpecProvider)
                        .build();

                httpClientBuilder.setDefaultCookieSpecRegistry(reg);
            }
        }

//		return httpclient;
        return httpClientBuilder.build();
    }

    private void ignoreSSLSocket1(Site site, HttpClientBuilder httpClientBuilder) {
        if (site.isSslSocketFactory()) {
            try {

                SSLContextBuilder sslContextBuilder = new SSLContextBuilder();
                SSLConnectionSocketFactory socketFactory = null;
                try {
                    sslContextBuilder.loadTrustMaterial(null, new TrustSelfSignedStrategy());
                    socketFactory = new SSLConnectionSocketFactory(sslContextBuilder.build(), SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
                } catch (NoSuchAlgorithmException e) {
                    logger.info("ignoreSSLSocket:", e);
                } catch (KeyStoreException e) {
                    logger.info("ignoreSSLSocket:", e);
                } catch (KeyManagementException e) {
                    logger.info("ignoreSSLSocket:", e);
                }
                httpClientBuilder.setSSLSocketFactory(socketFactory);
            } catch (ParseException e) {
                logger.info("ignoreSSLSocket:", e);
            }
        }
    }
    private void ignoreSSLSocket2(Site site, HttpClientBuilder httpClientBuilder) {
        if (site.isSslSocketFactory()) {
            X509TrustManager xtm = new X509TrustManager(){   //创建TrustManager
                public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {}
                public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {}
                public X509Certificate[] getAcceptedIssuers() { return null; }
            };
            try {
                SSLContext ctx = SSLContext.getInstance("TLS");

                ctx.init(null, new TrustManager[]{xtm}, new SecureRandom());
                SSLContext.setDefault(ctx);

                SSLConnectionSocketFactory sslSocketFactory = new SSLConnectionSocketFactory(ctx);

                //Protocol.registerProtocol("https", new Protocol("https", new EasySSLProtocolSocketFactory(), 443));
                //Protocol easyhttps = new Protocol("https", new EasySSLProtocolSocketFactory(), 443);
				/*HostnameVerifier verifier = new HostnameVerifier() {
                    @Override
                    public boolean verify(String arg0, SSLSession arg1) {
                        return true;
                    }
                }*/
                httpClientBuilder.setHostnameVerifier(SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
                httpClientBuilder.setSslcontext(ctx);
                httpClientBuilder.setSSLSocketFactory(sslSocketFactory);
            } catch (KeyManagementException e) {
                logger.info("ignoreSSLSocket:", e);
            } catch (NoSuchAlgorithmException e) {
                logger.info("ignoreSSLSocket:", e);
            } catch (ParseException e) {
                logger.info("ignoreSSLSocket:", e);
            }
        }
    }

    private void generateCookie(HttpClientBuilder httpClientBuilder, Site site) {
        CookieStore cookieStore = new BasicCookieStore();
        if (site != null) {
            SiteUtil.addCookieToStore(cookieStore, site);
        }
        CookieStoreUtil.putContextToCookieStore(cookieStore, 2);
        CookieStoreUtil.putContextToCookieStore(cookieStore, 1);
        httpClientBuilder.setDefaultCookieStore(cookieStore); //为当前httpclient对象添加cookie设置
    }
    public void close() {
        connectionManager.close();
    }
    static {
        Security.setProperty("jdk.certpath.disabledAlgorithms", "");
    }
}
