package com.bolo.crawler;

import org.apache.http.impl.cookie.BasicClientCookie;

import org.apache.http.client.CookieStore;
import java.util.Map;

/**
 * @Author wangyue
 * @Date 15:23
 */
public class SiteUtil {
    public static void addCookieToStore(CookieStore cookieStore,Site site){
        addCookieToStore(cookieStore,site);
    }

    private static void addCookieToStore1(CookieStore cookieStore,Site site){
        for (Map.Entry<String,String> cookieEntry : site.getCookies().entrySet()){
            BasicClientCookie cookie = new BasicClientCookie(cookieEntry.getKey(), cookieEntry.getValue());
            cookie.setDomain(site.getDomain());
            cookieStore.addCookie(cookie);
        }

        for (Map.Entry<String,Map<String,String>> domainEntry : site.getAllCookies().entrySet()){
            for (Map.Entry<String, String> cookieEntry : domainEntry.getValue().entrySet()) {
                BasicClientCookie cookie = new BasicClientCookie(cookieEntry.getKey(), cookieEntry.getValue());
                cookie.setDomain(domainEntry.getKey());
                cookieStore.addCookie(cookie);
            }
        }
    }

}
