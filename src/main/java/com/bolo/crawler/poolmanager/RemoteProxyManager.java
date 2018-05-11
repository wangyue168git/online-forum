package com.bolo.crawler.poolmanager;

import com.alibaba.fastjson.JSONObject;
import com.bolo.crawler.httpclient.ProxyResponse;
import com.bolo.crawler.entitys.Proxy;
import com.bolo.crawler.entitys.SimpleObject;
import com.bolo.crawler.interfaceclass.ProxyManager;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.Converter;
import org.apache.commons.beanutils.converters.DateConverter;
import org.apache.http.HttpHost;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;

import javax.annotation.PostConstruct;
import java.util.Map;

/**
 * @Author wangyue
 * @Date 15:54
 */
@Slf4j
@Component
public class RemoteProxyManager {

//    @Resource
//    RestTemplate restTemplate;

    private String proxy;

    @Value("#{proxy.proxy}")
    public void setProxy(String proxy) {
        System.out.println(proxy);
        this.proxy = proxy;
    }

    @PostConstruct
    public void init() {
        java.util.Date defaultValue = null;
        Converter converter = new DateConverter(defaultValue);
        ConvertUtils.register(converter, java.util.Date.class);
        ConvertUtils.register(converter, java.sql.Date.class);
        ProxyManagerFactory.initRemoteProxyManager(this);
        log.info("RemoteProxyManager init ...");
    }

    public boolean enableProxy() {
        return true;
    }

    public Proxy getProxy(SimpleObject context) {
        JSONObject request = new JSONObject(context.toMap());
        log.info("getProxy request is " + request.toJSONString());
        request.remove(ProxyManager.CONTEXT_PROXY);
        String notifyUrl = "";
        ProxyResponse returnJson = execProxyRequest(request, notifyUrl, "getProxy");
        HttpHost httpHost = new HttpHost(returnJson.getIp(),returnJson.getPort());
        Proxy proxy = new Proxy(httpHost);
        proxy.setProxyKey(returnJson.getProxyKey());
        proxy.setSite(returnJson.getSite());
        return proxy;
    }

    public boolean useProxy(SimpleObject context, Proxy proxy, boolean release) {
        JSONObject request = new JSONObject(context.toMap());
        request.remove(ProxyManager.CONTEXT_PROXY);
        request.put("release", release);
        String notifyUrl = "";
        ProxyResponse returnJson = execProxyRequest(request, notifyUrl, "useProxy");
        return returnJson.isRelease();
    }

    public boolean releaseProxy(SimpleObject context, Proxy proxy, boolean use) {
        JSONObject request = new JSONObject(context.toMap());
        request.remove(ProxyManager.CONTEXT_PROXY);
        request.put("proxyKey", proxy.getProxyKey());
        request.put("use", use);
        String notifyUrl = "";
        ProxyResponse returnJson = execProxyRequest(request, notifyUrl, "releaseProxy");
        return returnJson.isRelease();
    }

    /**
     *
     * @param context
     * @param proxy  代理
     * @param allSite true:所有站点都移除 , fasle:移除当前站点
     * @return
     */
    public boolean removeProxy(SimpleObject context, Proxy proxy, boolean allSite) {
        JSONObject request = new JSONObject(context.toMap());
        request.remove(ProxyManager.CONTEXT_PROXY);
        request.put("proxyKey", proxy.getProxyKey());
        request.put("allSite", allSite);
        String notifyUrl = "";
        ProxyResponse returnJson = execProxyRequest(request, notifyUrl, "releaseProxy");
        return returnJson.isRelease();
    }

    public void holdProxy(SimpleObject proxyContext) {
        JSONObject request = new JSONObject(proxyContext.toMap());
        request.remove(ProxyManager.CONTEXT_PROXY);
        String notifyUrl = "";
        execProxyRequest(request, notifyUrl, "holdProxy");
    }

    private ProxyResponse execProxyRequest(JSONObject request, String notifyUrl, String action) {
        boolean needDetailLog = false;
        if(needDetailLog) {
            log.info(action + " request:" + request.toJSONString());
        } else {
            log.info(action + " begin:");
        }
        ProxyResponse returnJson = null;
        try {
            HttpHeaders headers = new HttpHeaders();
            MediaType type = MediaType.parseMediaType("application/json");
            headers.setContentType(type);
            HttpEntity<Map> formEntity = new HttpEntity<>(request, headers);
            returnJson = null;
            log.info("execProxyRequest response is " + new Gson().toJson(returnJson));
        } catch (RestClientException e) {
            log.error(action + "error: " + request.toJSONString(), e);
        }
        return returnJson;
    }
}
