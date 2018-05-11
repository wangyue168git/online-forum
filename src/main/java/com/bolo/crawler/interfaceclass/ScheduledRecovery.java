package com.bolo.crawler.interfaceclass;

/**
 * @Author wangyue
 * @Date 15:52
 */
public interface ScheduledRecovery {
    String toKey();
    boolean recover();
    void updateTs(long ts);
}
