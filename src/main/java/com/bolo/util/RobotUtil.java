package com.bolo.util;

import com.bolo.crawler.SessionUtil;
import com.bolo.crawler.SimpleObject;
import com.bolo.crawler.StatusTracker;
import com.bolo.redis.RedisCacheUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Map;

/**
 * @Author wangyue
 * @Date 16:57
 */
public class RobotUtil {
    private static final String KEY_SESSION = "RobotUtil.status";
    protected boolean exitWhenComplete = true;
    public final static int STAT_INIT = StatusTracker.STAT_INIT;
    public final static int STAT_STOPPED = StatusTracker.STAT_STOPPED;
    public final static int STAT_STOPPED_FAIL = StatusTracker.STAT_STOPPED_FAIL;
    public final static int STAT_RUNNING = StatusTracker.STAT_RUNNING;
    public final static int STAT_LOGIN_SUC = StatusTracker.STAT_LOGIN_SUC;
    //public final static int STAT_LOGIN_FAIL = 40;
    //private static final Map<String, AtomicInteger> userStat = new HashMap<String, AtomicInteger>();
    private static final Object sync = new Byte[1];
    protected static Logger logger = LoggerFactory.getLogger(RobotUtil.class);
    private static final int DEFAULT_CACHE_DURATION = 40*60*1 ;//Redis.getHour(2);

    private static String genKey(String userId, String module, String account) {
		/*
		String userId = SessionUtil.getCurrentUser(request);
		if (userId == null) {
			userId = request.getParameter("userId");
		}*/
        return KEY_SESSION + "-" + userId + "-" + module + "-" + account;
    }
    public static boolean init(String userId, String module, String account) {
        return init1(userId, module, account);
    }
    private static boolean init1(String userId, String module, String account) {
        String businessKey = genKey(userId, module, account);
        SessionUtil.init(userId, module, account, businessKey);
        Map<String,Object> map = null;
        //synchronized (sync) {
        map = redisCacheUtil.getMapOrAddIfNotExist(businessKey, DEFAULT_CACHE_DURATION);
        //}
        if (map == null) {
            return true;
        } else {
            SessionUtil.initKeyCacheData(map);
        }
		/*AtomicInteger stat = (AtomicInteger) map.get(KEY_SESSION);
		if (stat == null) {
			stat = new AtomicInteger(STAT_INIT);
			map.put(KEY_SESSION, stat);
		}
		while (true) {
			int statNow = stat.get();
			if (statNow == STAT_RUNNING) {
				return false;
			}
			if (stat.compareAndSet(statNow, STAT_RUNNING)) {
				break;
			}
		}
		setCacheMap(businessKey, map);*/

        return true;
        //if (checkRunningStat)
    }
    public static void clearContext() {
        String businessKey = SessionUtil.getText(SessionUtil.CURRENT_BUSINESS_KEY);
        redisCacheUtil.setMapToRedis(businessKey, Collections.emptyMap(), 1);
    }
    public static void finish(Map<String, Object> map) {
        try {
            if (map != null && !map.isEmpty()) {
                String businessKey = SessionUtil.getText(SessionUtil.CURRENT_BUSINESS_KEY);
                Map rmap = RobotUtil.getCacheMap(null, businessKey);
                rmap.putAll(map);
                RobotUtil.setCacheMap(null, businessKey, rmap, DEFAULT_CACHE_DURATION);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void stop() {
        SimpleObject context = null;
        stop(context);
    }
    public static void stop(SimpleObject context) {
		/*String businessKey = null;
		if (context != null) {
			businessKey = context.getString(SessionUtil.CURRENT_BUSINESS_KEY);
		} else {
			businessKey = SessionUtil.getText(SessionUtil.CURRENT_BUSINESS_KEY);
		}*/
        SessionUtil.clear();
		/*Map<String,Object> map = null;
		map = getCacheMap(businessKey);
		if (map != null) {
			AtomicInteger stat = (AtomicInteger) map.get(KEY_SESSION);
			if (stat == null) {
				return;
			}
			if (stat.compareAndSet(STAT_RUNNING, STAT_STOPPED)) {
				map.remove(KEY_SESSION);
			} else {
				logger.info("Robot " + businessKey + " stop fail!");
			}
		}
		setCacheMap(businessKey, map);*/
    }
    public static void setCacheMap(String module, String businessKey, Map<String, Object> map) {
        setCacheMap(module, businessKey, map, DEFAULT_CACHE_DURATION);
    }
    public static void setCacheMap(String module, String businessKey, Map<String, Object> map, int duration) {
        setCacheMap1(module, businessKey, map, duration);
    }
    private static void setCacheMap1(String module, String businessKey, Map<String, Object> map, int duration) {
        if (module == null && businessKey == null) {
            throw new IllegalArgumentException("module和businessKey不能同时为Null");
        }
        if (businessKey == null) {
            businessKey = SessionUtil.getText(module + "-BusinessKey");
        }
        //synchronized (sync) {
        redisCacheUtil.setMapToRedis(businessKey, map, duration);
        //}
    }
    public static Map<String, Object> getCacheMap(String module, String businessKey) {
        return getCacheMap1(module, businessKey);
    }
    public static Map<String, Object> getCacheMap(String userId, String module, String account) {
        String businessKey = genKey(userId, module, account);
        return getCacheMap1(null, businessKey);
    }
    static RedisCacheUtil redisCacheUtil = new RedisCacheUtil();
    private static Map<String, Object> getCacheMap1(String module, String businessKey) {
        if (module == null && businessKey == null) {
            throw new IllegalArgumentException("module和businessKey不能同时为Null");
        }
        if (businessKey == null) {
            businessKey = SessionUtil.getText(SessionUtil.CURRENT_BUSINESS_KEY);
        }
        if (businessKey == null) {
            businessKey = SessionUtil.getText(module + "-BusinessKey");
        }
        Map<String, Object> map = null;
        //synchronized (sync) {
        map = redisCacheUtil.getMap(businessKey);
        //}
        return map;
    }
	/*public static void stop(HttpSession session) {
		AtomicInteger stat = getStatus(session);
		if (stat == null) {
			return;
		}
		if (stat.compareAndSet(STAT_RUNNING, STAT_STOPPED)) {
		} else {
			logger.info("Robot " + SessionUtil.getCurrentUser(session) + " stop fail!");
		}
	}
	public static boolean checkRunningStat(HttpServletRequest request) {
		AtomicInteger stat = getStatus(request);
		if (stat == null) {
			stat = new AtomicInteger(STAT_INIT);
			request.getSession().setAttribute(KEY_SESSION, stat);
		}
		while (true) {
			int statNow = stat.get();
			if (statNow == STAT_RUNNING) {
				return false;
			}
			if (stat.compareAndSet(statNow, STAT_RUNNING)) {
				break;
			}
		}
		return true;
	}
	public static boolean checkIfRunning(HttpServletRequest request) {
		AtomicInteger stat = getStatus(request);
		if (stat != null && stat.get() == STAT_RUNNING) {
			return true;
		}
		return false;
	}


	private static AtomicInteger getStatus(HttpSession session) {
		return (AtomicInteger) session.getAttribute(KEY_SESSION);
	}
	private static AtomicInteger getStatus(HttpServletRequest request) {
		return (AtomicInteger) request.getSession().getAttribute(KEY_SESSION);
	}
	public static void stop(String user) {
		AtomicInteger stat = getStatus(user, false);
		if (stat == null) {
			return;
		}
		int statNow = stat.get();
		if (statNow < STAT_RUNNING) {
			return;
		}
		if (stat.compareAndSet(statNow, STAT_STOPPED)) {
		} else {
			logger.info("Robot " + user + " stop fail!");
		}
	}
	public static boolean checkRunningStat(String user) {
		AtomicInteger stat = getStatus(user, true);
		while (true) {
			int statNow = stat.get();
			if (statNow >= STAT_RUNNING) {
				return false;
			}
			if (stat.compareAndSet(statNow, STAT_RUNNING)) {
				break;
			}
		}
		return true;
	}
	public static boolean checkStat(String user, int stat0) {
		AtomicInteger stat = getStatus(user, true);
		while (true) {
			int statNow = stat.get();
			if (statNow == stat0) {
				return false;
			}
			if (stat.compareAndSet(statNow, stat0)) {
				break;
			}
		}
		return true;
	}
	public static boolean checkIfRunning(String user) {
		AtomicInteger stat = getStatus(user, false);
		if (stat != null && stat.get() >= STAT_RUNNING) {
			return true;
		}
		return false;
	}
	public static int getStat(String user) {
		AtomicInteger stat = getStatus(user, false);
		if (stat != null) {
			return stat.get();
		}
		return -1;
	}


	private static AtomicInteger getStatus(String user, boolean addIfNull) {
		AtomicInteger ai = null;
		synchronized (sync) {
			ai = (AtomicInteger) userStat.get(user);
			if (addIfNull && ai == null) {
				ai = new AtomicInteger(STAT_INIT);
				userStat.put(user, ai);
			}
		}
		return ai;
	}*/
}
