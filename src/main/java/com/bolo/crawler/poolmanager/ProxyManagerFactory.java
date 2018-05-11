package com.bolo.crawler.poolmanager;

import com.bolo.crawler.entitys.Proxy;
import com.bolo.crawler.entitys.SimpleObject;
import com.bolo.crawler.entitys.Site;
import com.bolo.crawler.interfaceclass.ProxyManager;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @Author wangyue
 * @Date 15:54
 */
public class ProxyManagerFactory {

    protected static ProxyManager INSTANCE;
    protected static RemoteProxyManager remoteProxyManager;
    private static AtomicBoolean inited = new AtomicBoolean(false);

    public static final void initRemoteProxyManager(RemoteProxyManager pm) {
        remoteProxyManager = pm;
//        Spider.DEFAULT_RETRY_TIMES -= 1;
        Site.DEFAULT_TIME_OUT *= 2;
//        Spider.DEFAULT_SITE_RETRY_TIMES = 2;
        if (!inited.get()) {
            inited.set(pm.enableProxy());
        }
    }

    public static final RemoteProxyManager getRemoteProxyManager() {
        return remoteProxyManager;
    }

    public static final void initProxyManager(ProxyManager pm) {
        initProxyManager1(pm);
    }
    private static final void initProxyManager1(ProxyManager pm) {
        INSTANCE = pm; //new PrioritySequenceProxyManager(); //new DefaultProxyManager();
        INSTANCE.init(null);
//        Spider.DEFAULT_RETRY_TIMES -= 1;
        Site.DEFAULT_TIME_OUT *= 2;
//        Spider.DEFAULT_SITE_RETRY_TIMES = 2;
		/*INSTANCE.addProxy(new String[] {"139.217.4.210", "31288"});
    	INSTANCE.addProxy(new String[] {"139.217.5.155", "31288"});*/
        if (!inited.get()) {
            inited.set(pm.enableProxy());
        }
        //ScheduledProxyDiscoverer.startScheduledRecover();
    }
    public static final boolean useProxyManager() {
        return inited.get();
    }
    public static final ProxyManager getProxyManager() {
        return INSTANCE;
    }

    public static Proxy getProxy(SimpleObject context){
        return (Proxy)(context == null ? null : context.getObject(ProxyManager.CONTEXT_PROXY));
    }
    public static long getProxyDuration(SimpleObject context){
        Number num = context == null ? null : context.getNumber(ProxyManager.CONTEXT_PROXY_DURATION);
        return num == null ? -1 : num.longValue();
    }
    public static int getProxyStatusCode(SimpleObject context){
        Number num = context == null ? null : context.getNumber(ProxyManager.CONTEXT_PROXY_STATUS_CODE);
        return num == null ? -1 : num.intValue();
    }
    public static int getReuseInterval(SimpleObject context){
        Number num = context == null ? null : context.getNumber(ProxyManager.CONTEXT_REUSE_INTERVAL);
        return num == null ? -1 : num.intValue();
    }
    public static int getProxyMethod(SimpleObject context){
        Number num = context == null ? null : context.getNumber(ProxyManager.CONTEXT_PROXY_METHOD);
        return num == null ? -1 : num.intValue();
    }
    public static int getProxyMode(SimpleObject context){
        Number num = context == null ? null : context.getNumber(ProxyManager.CONTEXT_PROXY_MODE);
        return num == null ? -1 : num.intValue();
    }
    public static String getSite(SimpleObject context){
        return context == null ? null : context.getString(ProxyManager.CONTEXT_SITE);
    }
    public static Boolean getIsAuthenticate(SimpleObject context){
        Boolean value = context == null ? null : context.getBoolean(ProxyManager.CONTEXT_IS_AUTHENTICATE);
        return value == null ? false : value;
    }
}
