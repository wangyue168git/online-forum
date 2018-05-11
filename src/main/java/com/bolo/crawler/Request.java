package com.bolo.crawler;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.*;

/**
 * @Author wangyue
 * @Date 10:31
 */
public class Request implements Serializable{
    public static final String KEY_PRIORITY = "priority";
    private static final long serialVersionUID = 2062192774891352043L;
    protected Logger logger = LoggerFactory.getLogger(getClass());
    public static final String CYCLE_TRIED_TIMES = "_cycle_tried_times";
    public static final String STATUS_CODE = "statusCode";
    public static final String PROXY = "proxy";
    public static final String PROXY_SNO = "proxy_SNO";
    public static final String PROXY_STATUS = "proxy_status";
    public static final String RESPONSE_TIME = "resp_time";
    public static final String STREAM = "stream";
    public static final String OBJECT = "Object";
    public static final String EXTRAS = "Extras";

    public static final String REFRESH_HTTP_CLIENT = "RefreshHttpClient";
    public static final String REFRESH_HTTP_CLIENT_TRUE = "True";

    private String charset;
    private String url;
    private String postXml;
    private String postContentType;
    private String postContentTypeCharSet;
    private String method;
    private boolean useProxy;
    private long priority;

    private Map<String, Object> extras;
    private Map<String, String> haders;

    public Request() {
    }

    public Map<String, String> getHeaders() {
        return haders;
    }

    public Request(String url) {
        this.url = url;
    }
    public String getHeader(String key) {
        if (haders == null) {
            return null;
        }
        return haders.get(key);
    }

    public Request putHeader(String key, String value) {
        if (haders == null) {
            haders = new HashMap<String, String>();
        }
        haders.put(key, value);
        return this;
    }
    private NameValuePair[] nameValuePairs;
    public void setNameValuePairs(int index, String name, String value) {
        nameValuePairs[index] = new BasicNameValuePair(name, value);
    }
    public void setNameValuePairs(String[][] pairs) {
        int index = 0;
        nameValuePairs = new NameValuePair[pairs.length];
        for(String[] arr : pairs) {
            if (arr[0] == null) {
                continue;
            }
            nameValuePairs[index++] = new BasicNameValuePair(arr[0], arr[1]);
        }
        putExtra("nameValuePair", nameValuePairs);
    }

    public String getCharset() {
        return charset;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPostXml() {
        return postXml;
    }

    public void setPostXml(String postXml) {
        this.postXml = postXml;
    }

    public String getPostContentType() {
        return postContentType;
    }

    public void setPostContentType(String postContentType) {
        this.postContentType = postContentType;
    }

    public String getPostContentTypeCharSet() {
        return postContentTypeCharSet;
    }

    public void setPostContentTypeCharSet(String postContentTypeCharSet) {
        this.postContentTypeCharSet = postContentTypeCharSet;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public boolean isUseProxy() {
        return useProxy;
    }

    public void setUseProxy(boolean useProxy) {
        this.useProxy = useProxy;
    }

    public long getPriority() {
        return priority;
    }

    public void setPriority(long priority) {
        this.priority = priority;
    }

    public Map<String, Object> getExtras() {
        return extras;
    }

    public void setExtras(Map<String, Object> extras) {
        this.extras = extras;
    }

    public Map<String, String> getHaders() {
        return haders;
    }

    public void setHaders(Map<String, String> haders) {
        this.haders = haders;
    }

    public Object getExtra(String key) {
        if (extras == null) {
            return null;
        }
        return extras.get(key);
    }

    public Request putExtra(String key, Object value) {
        if (extras == null) {
            extras = new HashMap<String, Object>();
            extras.put("sequenceNo", "SNO");
        }
        extras.put(key, value);
        return this;
    }

    public void notifyObserver(int step, SimpleObject context) {
        notifyObserver(step, null, context);
    }
    private Collection<ProcessorObserver> observerList;
    public void notifyObserver(int step, Request request, SimpleObject context) {
        if (observerList == null) {
            return;
        }
        for(ProcessorObserver observer : observerList) {
            if (context != null) {
                context.put(ProcessorObserver.KEY_OBSERVER, observer);
            }
            try {
                if (step == 1) {
                    observer.beforeRequest(context);
                } else if (step == 2) {
                    observer.afterRequest(context);
                } else if (step == 3) {
                    observer.preparedData(context);
                } else if (step == 4) {
                    observer.breakRequest(request);
                }
            } catch (Exception e) {
                logger.error(step == 1 ? "before request" : ( step == 2 ? "after request" : (step == 4 ? "break request" : "prepared data")), e);
                context.put(ProcessorObserver.KEY_ERROR, e);
            }
        }
    }
    private Collection<String> clsSet = new HashSet<>();
    public Request addObjservers(ProcessorObserver observer) {
        if (observer == null) {
            return this;
        }
        if (observerList == null) {
            observerList = new ArrayList<>();
        }
        if (StringUtils.isNotBlank(observer.getClassification())) {
            clsSet.add(observer.getClassification());
        }
        observerList.add(observer);
        return this;
    }

    public boolean isClassification(String clf) {
        return clsSet.contains(clf);
    }

}
