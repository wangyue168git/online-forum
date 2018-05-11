package com.bolo.crawler.interfaceclass;

import com.bolo.crawler.entitys.Proxy;
import com.bolo.crawler.entitys.SimpleObject;

/**
 * @Author wangyue
 * @Date 15:53
 */
public interface ProxyManager {

    String SITE_HTTP = "http";
    String SITE_HTTPS = "https";
    String SITE_HTTP_HTTPS = "http_https";

    int MAX_PROXY_NUM = 1000;
    int MAX_HTTPS_PROXY_NUM = 300;
    int DEFAULT_REUSE_INTERVAL = 100;

    int PROXY_METHOD_UNIQUE = 10;
    int PROXY_METHOD_MULTI = 20;

    int PROXY_MODE_HTTP = 10;
    int PROXY_MODE_HTTPS = 20;
    int PROXY_MODE_HTTP_HTTPS = 30;

    int PROXY_HOLDER_NONE = 10;
    int PROXY_HOLDER_ONE = 20;

    String CONTEXT_IS_AUTHENTICATE = "isAuthenticate";
    String CONTEXT_SITE = "site";
    String CONTEXT_PROXY_METHOD = "proxyMethod";
    String CONTEXT_PROXY_MODE = "proxyMode";
    String CONTEXT_PROXY = "proxy";
    String CONTEXT_PROXY_KEY = "proxyKey";
    String CONTEXT_PROXY_DURATION = "proxyDuration";
    String CONTEXT_PROXY_STATUS_CODE = "proxyStatusCode";
    String CONTEXT_REUSE_INTERVAL = "reuseInterval";

    String CONTEXT_SEQUENCE_NO = "sequenceNo";
    String CONTEXT_LAST_SYNC_USE_PROXY_TS = "lastSyncUseProxyTs";
    // 代理每次请求之后的状态数组
    String CONTEXT_PROXY_STATUS_LIST = "proxyStatusList";
    String CONTEXT_PROXY_NEED_LOCK = "proxyNeedLock";
    String CONTEXT_SPIDER_LAST_STEP = "spiderLastStep";

    boolean init(SimpleObject context);
    boolean existsProxy(String[] httpProxyList);
    void addProxy(SimpleObject context, String[]... httpProxyList);
    //void addProxy(String site, SimpleObject context, String[]... httpProxyList);

    //void addProxy(SimpleObject context, String[]... httpProxyList);
    //void addProxy(String site, int reuseInterval, SimpleObject context, String[]... httpProxyList);
    Proxy getProxy(SimpleObject context);
    //返回为true，则代理可继续使用
    boolean useProxy(SimpleObject context, Proxy proxy, boolean release);
    boolean releaseProxy(SimpleObject context, Proxy proxy);
    void removeProxy(SimpleObject context, Proxy proxy);
    void removeProxy(String key);
    boolean readProxyList();
    void saveProxyList();
    int getIdleNum(String site);
    String allProxy();
    String allProxyStatus();
    String allProxySummaryInfo();
    String removedProxyStatus();

    boolean enableProxy();
}
