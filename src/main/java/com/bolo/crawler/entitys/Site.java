package com.bolo.crawler.entitys;

import com.bolo.crawler.interfaceclass.ProcessorObserver;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.httpclient.HttpHost;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.*;

/**
 * @Author wangyue
 * @Date 15:00
 */
@Getter
@Setter
public class Site implements Serializable{

    public static int DEFAULT_TIME_OUT = 10000;
    private String domain;
    private String siteName;
    private String detectorUrl;
    private int forbiddenSleepTime;
    private ProcessorObserver preventForbidden;

    private String userAgent;

    private Map<String, String> defaultCookies = new LinkedHashMap<String, String>();

    private Table<String, String, String> cookies = HashBasedTable.create();

    private String charset;

    private List<Request> startRequests = new ArrayList<Request>();

    private int sleepTime = 5000;
    private int rndSleepTime = 0;

    private int retryTimes = 0;

    private int cycleRetryTimes = 0;

    private int timeOut = DEFAULT_TIME_OUT;

    public static final Set<Integer> DEFAULT_STATUS_CODE_SET = new HashSet<Integer>();

    private Set<Integer> acceptStatCode = DEFAULT_STATUS_CODE_SET;

    private Map<String, String> headers = new HashMap<String, String>();

    private HttpHost httpProxy;

    private boolean cookiesManager = true;
    private boolean useGzip = true;
    private boolean shareCache;
    private boolean sslSocketFactory;
    private boolean followRedirect;
    private boolean socketProxy;
    // 是否需要锁定IP，移动商城一个IP 在同一时间只能有一个账号登录
    private boolean lockProxyOnStartUp;
    private String cookieSpec;
    private String sslProtocolContext;

    public boolean isLockProxyOnStartUp(){
        return lockProxyOnStartUp;
    }

    public String[][] getReplaceCharsWhenRedirect() {
        return replaceCharsWhenRedirect;
    }

    public void setReplaceCharsWhenRedirect(String[][] replaceCharsWhenRedirect) {
        this.replaceCharsWhenRedirect = replaceCharsWhenRedirect;
    }

    private String[][] replaceCharsWhenRedirect;

    public boolean isSocketProxy(){
        return socketProxy;
    }


    public boolean isFollowRedirect() {
        return followRedirect;
    }

    public void setFollowRedirect(boolean followRedirect) {
        this.followRedirect = followRedirect;
    }

    static {
        DEFAULT_STATUS_CODE_SET.add(200);
    }
    public boolean isSslSocketFactory() {
        return sslSocketFactory;
    }
    public void setSslSocketFactory(boolean sslSocketFactory) {
        this.sslSocketFactory = sslSocketFactory;
    }
    public static Site me() {
        Site me = new Site();
        me.setSleepTime(300);
        return me;
    }
    public Site addCookie(String name, String value) {
        defaultCookies.put(name, value);
        return this;
    }
    public Site addCookie(String domain, String name, String value) {
        cookies.put(domain, name, value);
        return this;
    }

    public Site setUserAgent(String userAgent) {
        this.userAgent = userAgent;
        return this;
    }
    public Map<String, String> getCookies() {
        return defaultCookies;
    }

    public Map<String,Map<String, String>> getAllCookies() {
        return cookies.rowMap();
    }

    public String getUserAgent() {
        return userAgent;
    }
    public String getDomain() {
        return domain;
    }

    public String getSiteName() {
        if(StringUtils.isEmpty(siteName)) {
            return getDomain();
        } else {
            return siteName;
        }
    }

    public Site setDomain(String domain) {
        this.domain = domain;
        return this;
    }
    public Site setCharset(String charset) {
        this.charset = charset;
        return this;
    }

    public String getCharset() {
        return charset;
    }

    public int getTimeOut() {
        return timeOut;
    }
    public Site setTimeOut(int timeOut) {
        this.timeOut = timeOut;
        return this;
    }
    public Site setAcceptStatCode(Set<Integer> acceptStatCode) {
        this.acceptStatCode = acceptStatCode;
        return this;
    }
    public Set<Integer> getAcceptStatCode() {
        return acceptStatCode;
    }
    public Site setSleepTime(int sleepTime) {
        this.sleepTime = sleepTime;
        return this;
    }
    public int getSleepTime() {
        return sleepTime;
    }
    public int getRetryTimes() {
        return retryTimes;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public Site addHeader(String key, String value) {
        headers.put(key, value);
        return this;
    }

    public Site setRetryTimes(int retryTimes) {
        this.retryTimes = retryTimes;
        return this;
    }

    public int getCycleRetryTimes() {
        return cycleRetryTimes;
    }

    public Site setCycleRetryTimes(int cycleRetryTimes) {
        this.cycleRetryTimes = cycleRetryTimes;
        return this;
    }

    public HttpHost getHttpProxy() {
        return httpProxy;
    }

    public Site setHttpProxy(HttpHost httpProxy) {
        this.httpProxy = httpProxy;
        return this;
    }

    public boolean isUseGzip() {
        return useGzip;
    }
    public Site setUseGzip(boolean useGzip) {
        this.useGzip = useGzip;
        return this;
    }
    public boolean isShareCache() {
        return shareCache;
    }

    public Site setShareCache(boolean shareCache) {
        this.shareCache = shareCache;
        return this;
    }

    /*public Task toTask() {
        return new Task() {
            @Override
            public String getUUID() {
                return Site.this.getDomain();
            }

            public Site getSite() {
                return Site.this;
            }
        };
    }*/

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Site site = (Site) o;

        if (cycleRetryTimes != site.cycleRetryTimes) return false;
        if (retryTimes != site.retryTimes) return false;
        if (sleepTime != site.sleepTime) return false;
        if (timeOut != site.timeOut) return false;
        if (acceptStatCode != null ? !acceptStatCode.equals(site.acceptStatCode) : site.acceptStatCode != null)
            return false;
        if (charset != null ? !charset.equals(site.charset) : site.charset != null) return false;
        if (defaultCookies != null ? !defaultCookies.equals(site.defaultCookies) : site.defaultCookies != null)
            return false;
        if (domain != null ? !domain.equals(site.domain) : site.domain != null) return false;
        if (headers != null ? !headers.equals(site.headers) : site.headers != null) return false;
        if (startRequests != null ? !startRequests.equals(site.startRequests) : site.startRequests != null)
            return false;
        if (userAgent != null ? !userAgent.equals(site.userAgent) : site.userAgent != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = domain != null ? domain.hashCode() : 0;
        result = 31 * result + (userAgent != null ? userAgent.hashCode() : 0);
        result = 31 * result + (defaultCookies != null ? defaultCookies.hashCode() : 0);
        result = 31 * result + (charset != null ? charset.hashCode() : 0);
        result = 31 * result + (startRequests != null ? startRequests.hashCode() : 0);
        result = 31 * result + sleepTime;
        result = 31 * result + retryTimes;
        result = 31 * result + cycleRetryTimes;
        result = 31 * result + timeOut;
        result = 31 * result + (acceptStatCode != null ? acceptStatCode.hashCode() : 0);
        result = 31 * result + (headers != null ? headers.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Site{" +
                "domain='" + domain + '\'' +
                ", userAgent='" + userAgent + '\'' +
                ", cookies=" + defaultCookies +
                ", charset='" + charset + '\'' +
                ", startRequests=" + startRequests +
                ", sleepTime=" + sleepTime +
                ", retryTimes=" + retryTimes +
                ", cycleRetryTimes=" + cycleRetryTimes +
                ", timeOut=" + timeOut +
                ", acceptStatCode=" + acceptStatCode +
                ", headers=" + headers +
                '}';
    }
    public int getRndSleepTime() {
        return rndSleepTime;
    }
    public void setRndSleepTime(int rndSleepTime) {
        this.rndSleepTime = rndSleepTime;
    }
    public String getDetectorUrl() {
        return detectorUrl;
    }
    public void setDetectorUrl(String detectorUrl) {
        this.detectorUrl = detectorUrl;
    }
    public boolean isCookiesManager() {
        return cookiesManager;
    }
    public void setCookiesManager(boolean cookiesManager) {
        this.cookiesManager = cookiesManager;
    }
    public String getCookieSpec() {
        return cookieSpec;
    }
    public void setCookieSpec(String cookieSpec) {
        this.cookieSpec = cookieSpec;
    }
    public int getForbiddenSleepTime() {
        return forbiddenSleepTime;
    }
    public void setForbiddenSleepTime(int forbiddenSleepTime) {
        this.forbiddenSleepTime = forbiddenSleepTime;
    }
    public ProcessorObserver getPreventForbidden() {
        return preventForbidden;
    }
    public void setPreventForbidden(ProcessorObserver preventForbidden) {
        this.preventForbidden = preventForbidden;
    }
    public String getSslProtocolContext() {
        return sslProtocolContext;
    }
    public void setSslProtocolContext(String sslProtocolContext) {
        this.sslProtocolContext = sslProtocolContext;
    }

}
