package com.bolo.crawler.utils;

import org.apache.http.client.CookieStore;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;

/**
 * @Author wangyue
 * @Date 15:20
 */
public class CookieStoreUtil {

    public static final String CURRENT_COOKIESTORE = "CookieStoreUtil.cookieStore.current";
    public static final String INIT_COOKIESTORE = "CookieStoreUtil.cookieStore.init";
    private static final String[] arrays = new String[]{CURRENT_COOKIESTORE, INIT_COOKIESTORE};

    public static CookieStore addCookieStoreToContext(CookieStore cookieStore, int t) {
        return addCookieStoreToContext1(cookieStore, t);
    }
    private static CookieStore addCookieStoreToContext1(CookieStore cookieStore, int t) {
        CookieStore cs = (CookieStore) SessionUtil.getObject(arrays[t - 1]);
        if (cs == null) {
            cs =  new BasicCookieStore();
        }
        if (cookieStore != null) {
            for(Cookie c : cookieStore.getCookies()) {
                cs.addCookie(c);
            }
        }
        SessionUtil.setObject(arrays[t - 1], cs);
        return cs;
    }
    public static void removeContextCookieStore(int t) {
        SessionUtil.setObject(arrays[t - 1], null);
    }
    public static CookieStore putContextToCookieStore(CookieStore cookieStore, int t) {
        return putContextToCookieStore1(cookieStore, t);
    }
    private static CookieStore putContextToCookieStore1(CookieStore cookieStore, int t) {
        CookieStore cs = (CookieStore) SessionUtil.getObject(arrays[t - 1]);
        if (cs == null) {
            return cs;
        }
        if (cookieStore != null) {
            for(Cookie c : cs.getCookies()) {
                cookieStore.addCookie(c);
            }
        }
        return cs;
    }
}
