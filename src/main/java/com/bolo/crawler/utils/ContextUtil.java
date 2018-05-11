package com.bolo.crawler.utils;


import com.alibaba.fastjson.JSON;
import com.bolo.crawler.entitys.Request;
import com.bolo.crawler.entitys.SimpleObject;
import com.bolo.crawler.interfaceclass.ProcessorObserver;
import com.bolo.crawler.interfaceclass.Task;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.CookieStore;
import org.apache.http.cookie.Cookie;
import org.apache.http.cookie.SetCookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.InputStream;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author wangyue
 * @Date 16:01
 */
public class ContextUtil {

    protected static Logger logger = LoggerFactory.getLogger(ContextUtil.class);

    /**
     * 获取响应时间
     * @author think
     * @param context context 上下文对象
     * @return 毫秒数的long
     */
    public static long getResponseTime(SimpleObject context){
        return context.getNumber(ProcessorObserver.KEY_RESP_TIME).longValue();
    }
    public static int getRetryTimes(SimpleObject context){
        return context.getNumber(ProcessorObserver.KEY_TIMES).intValue();
    }
    /**
     * 获取Task对象
     * @author think
     * @param context context 上下文对象
     * @return
     */
    public static Task getTask(SimpleObject context){
        return (Task) context.getObject(ProcessorObserver.KEY_TASK);
    }
    /**
     * 获取Response对象
     * @author think
     * @param context context 上下文对象
     * @return
     */
    public static HttpResponse getResponse(SimpleObject context){
        return (HttpResponse) context.getObject(ProcessorObserver.KEY_HTTPRESPONSE);
    }
    /**
     * 获取抛出的exception，如有
     * @author think
     * @param context context 上下文对象
     * @return
     */
    public static Exception getError(SimpleObject context){
        return (Exception) context.getObject(ProcessorObserver.KEY_ERROR);
    }
    public static InputStream getInputStream(SimpleObject context){
        return (InputStream) context.getObject(ProcessorObserver.KEY_STREAM);
    }
    /**
     * 获取响应内容，纯文本
     * @author think
     * @param context context 上下文对象
     * @return
     */
    public static String getContent(SimpleObject context){
        return context.getString(ProcessorObserver.KEY_CONTENT);
    }
    public static Object getObject(SimpleObject context){
        return context.getObject(ProcessorObserver.KEY_OBJECT);
    }
    public static ProcessorObserver getProcessorObserver(SimpleObject context){
        return (ProcessorObserver) context.getObject(ProcessorObserver.KEY_OBSERVER);
    }
    public static CookieStore getCookieStore(SimpleObject context){
        CookieStore cookieStore = (CookieStore) context.getObject(ProcessorObserver.KEY_COOKIES);
        return cookieStore;
    }
    public static void removeAllCookie(SimpleObject context) {
        CookieStore cs = getCookieStore(context);
        cs.clear();
    }
    /**
     * 获取此时响应时的某个cookie值
     * @author think
     * @param context context 上下文对象
     * @return
     */
    public static String getCookieValue(SimpleObject context, String name){
        return getCookieValue(context, name, null);
    }
    /**
     * 修改或新加cookie的值
     * @author think
     * @param context context 上下文对象
     * @param name
     * @param addIfNotExists
     * @param params [value, path, domain]
     * @return
     */
    public static boolean setCookieValue(SimpleObject context, String name, boolean addIfNotExists, String params[]){
        CookieStore cookieStore = (CookieStore) context.getObject(ProcessorObserver.KEY_COOKIES);
        if(cookieStore == null){
            cookieStore = new BasicCookieStore();
        }
        String value = params != null && params.length >= 1 ? params[0] : null;
        String path = params != null && params.length >= 2 ? params[1] : null;
        String domain = params != null && params.length >= 3 ? params[2] : null;

        Cookie c = getCookie(name, domain, cookieStore);
        boolean result = false;
        //当找不到对应name的cookie时，是否要创建新的cokkie
        //revised think 2014-11-14 [comment] 根据param相关的值创建cookie
        if (c == null && addIfNotExists) {
            BasicClientCookie cookie = new BasicClientCookie(name, value);
            cookie.setDomain(domain);
            cookie.setPath(path);
            cookie.setExpiryDate(new Date(System.currentTimeMillis() + 60 * 60 * 1000));
            cookieStore.addCookie(cookie);
            result = true;
        } else if (c != null) {
            if (c instanceof SetCookie) {
                SetCookie sc = (SetCookie) c;
                sc.setValue(value);
                result = true;
            } else {
                //revised think 2014-11-14
                BasicClientCookie cookie = new BasicClientCookie(c.getName(), c.getValue());
                cookie.setDomain(domain != null ? domain : c.getDomain());
                cookie.setPath(path != null ? path : c.getPath());
                cookie.setExpiryDate(new Date(System.currentTimeMillis() + 60 * 60 * 1000));
                cookieStore.addCookie(cookie);
            }
        }
        CookieStoreUtil.addCookieStoreToContext(cookieStore, 1);
        return result;
    }
    public static boolean setCookieValues(SimpleObject context, String[] names, boolean addIfNotExists, String paramss[][]){
        CookieStore cookieStore = (CookieStore) context.getObject(ProcessorObserver.KEY_COOKIES);
        int i = 0;
        boolean result = true;
        for(String name : names) {
            String[] params = paramss[i ++];
            String value = params != null && params.length >= 1 ? params[0] : null;
            String path = params != null && params.length >= 2 ? params[1] : null;
            String domain = params != null && params.length >= 3 ? params[2] : null;

            Cookie c = getCookie(name, domain, cookieStore);

            //当找不到对应name的cookie时，是否要创建新的cokkie
            //revised think 2014-11-14 [comment] 根据param相关的值创建cookie
            if (c == null && addIfNotExists) {
                BasicClientCookie cookie = new BasicClientCookie(name, value);
                cookie.setDomain(domain);
                cookie.setPath(path);
                cookieStore.addCookie(cookie);
                //result = result && true;
            } else if (c != null) {
                if (c instanceof SetCookie) {
                    SetCookie sc = (SetCookie) c;
                    sc.setValue(value);
                    //result = result && true;
                } else {
                    //revised think 2014-11-14
                    BasicClientCookie cookie = new BasicClientCookie(c.getName(), c.getValue());
                    cookie.setDomain(domain != null ? domain : c.getDomain());
                    cookie.setPath(path != null ? path : c.getPath());
                    cookieStore.addCookie(cookie);
                }
            }
        }
        CookieStoreUtil.addCookieStoreToContext(cookieStore, 1);
        return result;
    }
    public static String getCookieValue(SimpleObject context, String name, String domain){
        CookieStore cookieStore = (CookieStore) context.getObject(ProcessorObserver.KEY_COOKIES);
        return getCookieValue(name, domain, cookieStore);
    }
    public static String getCookieValue(String name, String domain, CookieStore cookieStore) {
        Cookie c = getCookie(name, domain, cookieStore);
        return c == null ? null : c.getValue();
    }
    public static Cookie getCookie(String name, String domain, CookieStore cookieStore) {
        Collection<Cookie> cset = cookieStore.getCookies();
        for(Cookie c : cset) {
            if (name.equalsIgnoreCase(StringUtils.trim(c.getName())) && (domain == null || domain.equalsIgnoreCase(c.getDomain()))) {
                return c;
            }
        }
        return null;
    }
    /**
     * 获取响应document对象
     * @author think
     * @param context context 上下文对象
     * @return
     */
    public static Document getDocumentOfContent(SimpleObject context){
        String c = getContent(context);
        Document doc  = null;
        if (!StringUtils.isBlank(c)) {
            try {
                doc = Jsoup.parse(c);
            } catch (Exception e) {
                logger.error("getDocumentOfContent", e);
            }
        }
        return doc;
    }
    /**
     * 获取响应的json对象，如果不是json内容，则为null
     * @author think
     * @param context context 上下文对象
     * @return
     */
    public static JSONObject getJsonOfContent(SimpleObject context) {
        String c = getContent(context);
        return getJsonOfString(c);
    }

    public static JSONObject getJsonOfString(String c) {
        JSONObject json = null;
        if (!StringUtils.isBlank(c)) {
            try {
                json = new JSONObject(c);
            } catch (Exception e) {
                logger.error("getJsonOfContent", e);
            }
        }
        return json;
    }

    public static <T> T getObjectOfContent(SimpleObject context, Class<T> clazz) {
        return getObjectOfContent(getContent(context), clazz);
    }
    public static <T> T getObjectOfContent(String json, Class<T> clazz) {
        return JSON.parseObject(json, clazz);
    }
    /**
     * 获取响应的json数组，如果不是json内容，则为null
     * @author think
     * @param context context 上下文对象
     * @return
     */
    public static JSONArray getJsonArrayOfContent(SimpleObject context) {
        String c = getContent(context);
        JSONArray json = null;
        if (!StringUtils.isBlank(c)) {
            try {
                json = new JSONArray(c);
            } catch (Exception e) {
                logger.error("getJsonArrayOfContent", e);
            }
        }
        return json;
    }
    /**
     * 获取此次的request
     * @author think
     * @param context context 上下文对象
     * @return
     */
    public static Request getRequest(SimpleObject context){
        return (Request) context.getObject(ProcessorObserver.KEY_REQUEST);
    }
    public static void retry(SimpleObject context, boolean retry){
        context.put(ProcessorObserver.KEY_RETRY, retry);
    }
    public static boolean isRetry(SimpleObject context){
        Boolean retry = context.getBoolean(ProcessorObserver.KEY_RETRY);
        return retry != null && retry ;
    }

    public static SimpleObject needRefreshClient(SimpleObject so){
        if(so == null) {
            so = new SimpleObject();
        }
        Map<String, Object> extra = (Map<String, Object>) so.getObject(Request.EXTRAS);
        if(extra == null) {
            extra = new HashMap<>();
            so.put(Request.EXTRAS, extra);
        }
        extra.put(Request.REFRESH_HTTP_CLIENT, Request.REFRESH_HTTP_CLIENT_TRUE);
        return so;
    }
}
