package com.bolo.crawler;

/**
 * @Author wangyue
 * @Date 15:51
 */
public abstract class AbstractScheduledRecovery implements ScheduledRecovery{

    protected long recoveryUpdateTs;

    @Override
    public void updateTs(long ts) {
        recoveryUpdateTs = ts;
    }
}
