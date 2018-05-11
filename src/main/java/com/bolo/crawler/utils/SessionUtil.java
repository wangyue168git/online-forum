package com.bolo.crawler.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author wangyue
 * @Date 15:21
 */
public class SessionUtil {

    protected Logger logger = LoggerFactory.getLogger(getClass());

    public static final String CURRENT_REQUEST = "currentRequest";
    public static final String CURRENT_MODULE = "currentModule";
    public static final String CURRENT_ACCOUNT = "currentAccount";
    public static final String CURRENT_BUSINESS_KEY = "currentBusinessKey";
    public static final String CURRENT_DATE = "currentDate";
    public static final String CURRENT_USER = "currentUser";
    public static final String CURRENT_CONTEXT = "currentContext";
    public static final String CURRENT_DATA = "currentData";
    public static final String CURRENT_CERT_ID = "currentCertId";

    public static String getCurrentUser(HttpServletRequest request) {
        return getCurrentUser(request.getSession());
    }
    public static String getCurrentUser(HttpSession session) {
        return (String) session.getAttribute("currentUser");
    }

    private static ThreadLocal SESSIONS = new ThreadLocal() {
        protected Object initialValue() {
            return new HashMap();
        }
    };
    public static HttpServletRequest getRequest() {
        return getRequest(true, true);
    }

    public static HttpServletRequest getRequest(boolean doLog) {
        return getRequest(doLog, true);
    }

    public static HttpServletRequest getRequest(boolean doLog, boolean isThrowException) {
        HttpServletRequest req = (HttpServletRequest) ((Map) SESSIONS.get()).get(CURRENT_REQUEST);

        return req;
    }
    public static String getText(String key) {
        return (String) ((Map) SESSIONS.get()).get(key);
    }
    public static Object getObject(String key) {
        return ((Map) SESSIONS.get()).get(key);
    }
    public static void setObject(String key, Object obj) {
        ((Map) SESSIONS.get()).put(key, obj);
    }
    public static HttpSession getSession() {
        HttpServletRequest request = getRequest();
        HttpSession session = request.getSession();

        // Because every login user should have an AclContext in Session, which
        // represents already login.
        // When this attribute is null, can implicitly say that he/she is logout.
        // Though this is not a
        // formal way, but in order to reduce the support work load, we use this
        // to identify the concurrent
        // click & logout problem.
        return session;
    }
    /*
    public static User getCurrentUser() {
        HttpServletRequest request = getRequest(true, false);
        if (request != null) {
            return getCurrentUser(request);
        } else {
            Map map = (Map) SESSIONS.get();
            return (User) map.get(CURRENT_USER);
        }

    }*/
    public static void initKeyCacheData(Map<String,Object> map1) {
        Map map = (Map) SESSIONS.get();
        map.put(CURRENT_DATA, map1);
    }
    public static void init(String userId, String module, String account, String businessKey) {
        init1(userId, module, account, businessKey);
    }
    private static void init1(String userId, String module, String account, String businessKey) {
        Map map = (Map) SESSIONS.get();

        if (map != null) {
            if (!map.isEmpty()) {
                // LOG.warn("SESSIONS of SessionUtil is not empty!!!");
            }
            //map.put(CURRENT_REQUEST, request);
            map.put(CURRENT_MODULE, module);
            map.put(CURRENT_ACCOUNT, account);
            map.put(CURRENT_BUSINESS_KEY, businessKey);
            map.put(module + "-BusinessKey", businessKey);
        }
    }
    public static void clear() {
        Map map = (Map) SESSIONS.get();
        if (map != null) {
            map.clear();
        }
    }

}
