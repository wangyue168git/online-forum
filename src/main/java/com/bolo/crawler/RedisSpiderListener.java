package com.bolo.crawler;

import com.bolo.util.RobotUtil;
import org.apache.http.client.CookieStore;
import org.apache.http.impl.client.BasicCookieStore;

import java.util.Map;

/**
 * @Author wangyue
 * @Date 16:48
 */
public class RedisSpiderListener  extends AbstractSpiderListener{
    private static final String KEY_REDIS_COOKIES = "RedisSpiderListener.cookies";
    public static final String KEY_CACHE_DATA = "RedisSpiderListener.data";
    public static final String KEY_PROXY_HOLDER = "RedisSpiderListener.proxy";
    public static final String KEY_PROXY_HOLDER_KEY = "RedisSpiderListener.proxy.key";

    private String businessKey;
    private Map dataMap;
    private int expired = 40 * 60;

    public RedisSpiderListener(SimpleObject context) {
        super(context);
        businessKey = context.getString(SessionUtil.CURRENT_BUSINESS_KEY);
        dataMap = (Map) context.getObject(SessionUtil.CURRENT_DATA);
    }
    @Override
    public void onStartup(SimpleObject context, Object obj) {
        onStartup1(context, obj);
    }
    private void onStartup1(SimpleObject context, Object obj) {
        if (businessKey == null) {
            return;
        }
        boolean isCookies = ContextUtil.getTask(context).getSite().isCookiesManager();
        SessionUtil.setObject(SessionUtil.CURRENT_BUSINESS_KEY, businessKey);
        if (isCookies) {
            CookieStoreUtil.removeContextCookieStore(1);
            CookieStoreUtil.removeContextCookieStore(2);
        }
        Map<String,Object> map = null;
        map = RobotUtil.getCacheMap(null, businessKey);
        if (map != null) {
            putCrawlerEntity(obj, map);
            if (isCookies) {
                CookieStore cs = (CookieStore) map.get(KEY_REDIS_COOKIES);
                if (cs == null) {
                    return;
                }
                CookieStoreUtil.addCookieStoreToContext(cs, 2);
            }
        }
    }
    public static void putCrawlerEntity(Object obj, Map<String, Object> map) {
        if (obj instanceof AbstractCrawler) {
            AbstractCrawler ac = (AbstractCrawler) obj;
            SimpleObject entity = (SimpleObject) map.get(KEY_CACHE_DATA);
            if (entity != null) {
                ac.getEntity().putAll(entity);
                //ac.printEntityData();
            }
            Object proxy = map.get(KEY_PROXY_HOLDER);
            if (proxy != null) {
                if (!proxy.toString().contains("54.223.60.17")) {
                    ac.getSpider().setHolderProxy(proxy);
                }
            }
            String certId = (String) map.get(SessionUtil.CURRENT_CERT_ID);
            ac.setCertId(certId);
            ac.getSpider().setCertId(certId);
        }
    }

    @Override
    public void onEvent(String event, SimpleObject context, Object obj) {
        onEvent1(event, context, obj);
    }
    private void onEvent1(String event, SimpleObject context, Object obj) {
        if (businessKey != null) {
            if ("saveEntity".equalsIgnoreCase(event)) {
                Map<String, Object> map = RobotUtil.getCacheMap(null, businessKey);
                if (map != null) {
                    //cache status
                    if (obj instanceof AbstractCrawler) {
                        AbstractCrawler ac = (AbstractCrawler) obj;
                        SimpleObject entity = (SimpleObject) map.get(KEY_CACHE_DATA);
                        if (entity != null) {
                            entity.putAll(ac.getEntity());
                        } else {
                            entity = ac.getEntity();
                        }
                        map.put(KEY_CACHE_DATA, entity);
                        //ac.printEntityData();
                        if (ContextUtil.getTask(context).getSite().isCookiesManager()) {
                            //cache cookies
                            CookieStore cs = (CookieStore) map.get(KEY_REDIS_COOKIES);
                            if (cs == null) {
                                cs = new BasicCookieStore();
                            }
                            CookieStoreUtil.putContextToCookieStore(cs, 1);
                            map.put(KEY_REDIS_COOKIES, cs);
                        }
                        putProxy(map, ac);
                    }
                    RobotUtil.setCacheMap(null, businessKey, map, expired);
                }
            }
        }
    }

    @Override
    public void onComplete(SimpleObject context, Object obj) {
        onComplete1(context, obj);
    }
    private void onComplete1(SimpleObject context, Object obj) {
        if (obj instanceof StatusTracker) {
            StatusTracker st = (StatusTracker) obj;
            st.notifyStatus();
        }
        SessionUtil.setObject(SessionUtil.CURRENT_BUSINESS_KEY, null);
        boolean isCookies = ContextUtil.getTask(context).getSite().isCookiesManager();

        if (businessKey != null) {
            Map<String,Object> map = null;
            map = RobotUtil.getCacheMap(null, businessKey);
            if (map != null) {
                int ex = expired;
                if (isCookies) {
                    //cache cookies
                    CookieStore cs = (CookieStore) map.get(KEY_REDIS_COOKIES);
                    if (cs == null) {
                        cs = new BasicCookieStore();
                    }
                    CookieStoreUtil.putContextToCookieStore(cs, 1);
                    map.put(KEY_REDIS_COOKIES, cs);
                }
                //cache status
                if (obj instanceof AbstractCrawler) {
                    AbstractCrawler ac = (AbstractCrawler) obj;
                    SimpleObject entity = (SimpleObject) map.get(KEY_CACHE_DATA);
                    if (entity != null) {
                        entity.putAll(ac.getEntity());
                    } else {
                        entity = ac.getEntity();
                    }
                    map.put(KEY_CACHE_DATA, entity);
                    putProxy(map, ac);
                    if (ac.getData().getString("TP_SWITCH_TYPE") != null || ac.getData().getBoolean("ClcContext") != null) {
                        ex = 1;
                    }
                    //ac.printEntityData();
                }

                RobotUtil.setCacheMap(null, businessKey, map, ex);
            }
            if (isCookies) {
                CookieStoreUtil.removeContextCookieStore(1);
                CookieStoreUtil.removeContextCookieStore(2);
            }
        }


    }

    private void putProxy(Map<String, Object> map, AbstractCrawler ac) {
        ScheduledRecovery holderProxy = (ScheduledRecovery) ac.getSpider().getHolderProxy();
        if (dataMap != null && holderProxy != null) {
            dataMap.put(Constant.KEY_DATA_CONTEXT_NO, holderProxy.toKey());
        }
        final boolean existHolder = map.containsKey(KEY_PROXY_HOLDER);
        if (ProxyManager.PROXY_HOLDER_ONE == ac.getSpider().getProxyHolder()) {
            //holderProxy =
            map.put(KEY_PROXY_HOLDER, holderProxy);
        } else if (ProxyManager.PROXY_HOLDER_NONE == ac.getSpider().getProxyHolder() || (ac.getSpider().getRecoverProxyWhenComplete() > 0 && ac.isSuccess())) {
            if (holderProxy != null && holderProxy.recover()) {
                holderProxy = null;
            }
        }
        try {
            if (holderProxy != null) {
                if (!existHolder) {
                    //ScheduledProxyDiscoverer.addScheduledRecovery(holderProxy);
                } else {
                    //ScheduledProxyDiscoverer.updateScheduledRecovery(holderProxy);
                }
            }
        } catch (Exception e) {
            logger.error("scheduledRecovery", e);
        }
    }
}
