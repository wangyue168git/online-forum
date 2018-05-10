package com.bolo.crawler;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.http.HttpHost;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Author wangyue
 * @Date 15:51
 */
public class Proxy extends AbstractScheduledRecovery implements Delayed, Serializable {
    protected transient Logger logger = LoggerFactory.getLogger(getClass());
    //ConnectException BindException SocketException EOFException NoRouteToHostException, PortUnreachableException
    //ProtocolException, RemoteException, SaslException, SocketException, SSLException, SyncFailedException, UnknownHostException,
    //UnknownServiceException, UnsupportedDataTypeException, UnsupportedEncodingException, UserPrincipalNotFoundException, UTFDataFormatException, ZipException
    private static final long serialVersionUID = 228939737383625551L;

    public static final int ERROR_403 = 403;
    public static final int ERROR_404 = 404;
    public static final int ERROR_BANNED = 10000;// banned by website
    public static final int ERROR_PROXY = 10001;// the proxy itself failed
    public static final int SUCCESS = 200;

    private final HttpHost httpHost;

    public String getProxyKey() {
        return proxyKey;
    }

    public void setProxyKey(String proxyKey) {
        this.proxyKey = proxyKey;
    }

    private String proxyKey;
    private int reuseTimeInterval = ProxyManager.DEFAULT_REUSE_INTERVAL;// ms
    private Long canReuseTime = 0L;
    private Long lastBorrowTime = System.currentTimeMillis();
    private Long lastUseTime = System.currentTimeMillis();
    private int factor = 10;
    //private BlockingQueue<Number> responeTimeQueue = new ArrayBlockingQueue<Number>(factor);
    private Long responseTime = 0L;

    private int failedNum = 0;
    private int successNum = 0;
    private int borrowNum = 0;

    //private Map<String, Proxy> allProxy = new ConcurrentHashMap<String, Proxy>();
    private String site;
    private List<Integer> failedErrorType = new ArrayList<Integer>();
    private long recoveryDuration = 15 * 60 * 1000;
    protected AtomicInteger counter = new AtomicInteger();
    public Proxy(HttpHost httpHost) {
        this.httpHost = httpHost;
        this.canReuseTime = System.nanoTime() + TimeUnit.NANOSECONDS.convert(reuseTimeInterval, TimeUnit.MILLISECONDS);
    }

    public Proxy(HttpHost httpHost, int reuseInterval) {
        this.httpHost = httpHost;
        this.canReuseTime = System.nanoTime() + TimeUnit.NANOSECONDS.convert(reuseInterval, TimeUnit.MILLISECONDS);
        reuseTimeInterval = reuseInterval;
    }
    private void init() {

    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        Proxy proxy = (Proxy) o;

        return new EqualsBuilder()
                .append(httpHost.getAddress().getHostAddress(), proxy.httpHost.getAddress().getHostAddress())
                .append(httpHost.getPort(), proxy.httpHost.getPort())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(httpHost.getAddress().getHostAddress())
                .append(httpHost.getPort())
                .toHashCode();
    }

    public int getSuccessNum() {
        return successNum;
    }

    public void successNumIncrement(int increment) {
        this.successNum += increment;
    }

    public Long getLastUseTime() {
        return lastUseTime;
    }

    public void setLastBorrowTime(Long lastBorrowTime) {
        this.lastBorrowTime = lastBorrowTime;
    }

    public void recordResponse(long duration) {
        recordResponse1(duration);
    }
    private void recordResponse1(long duration) {
        //this.responseTime = (System.currentTimeMillis() - lastBorrowTime + responseTime) / 2;
        this.lastBorrowTime = System.currentTimeMillis();
        this.lastUseTime = System.currentTimeMillis();
		/*try {
			if (responeTimeQueue.size() >= factor) {
				responeTimeQueue.take();
			}
			responeTimeQueue.put(duration);
		} catch (InterruptedException e) {
			logger.info("recordResponse", e);
		}
		this.responseTime = avg(responeTimeQueue);*/
    }
    private long avg(BlockingQueue<Number> queue) {
        int size = queue.size();
        Iterator<Number> it = queue.iterator();
        long total = 0;
        while (it.hasNext()) {
            total += it.next().longValue();
        }
        return total / size;
    }
    public List<Integer> getFailedErrorType() {
        return failedErrorType;
    }

    public void setFailedErrorType(List<Integer> failedErrorType) {
        this.failedErrorType = failedErrorType;
    }

    public void fail(int failedErrorType) {
        this.failedNum++;
        this.failedErrorType.add(failedErrorType);
    }

    public void setFailedNum(int failedNum) {
        this.failedNum = failedNum;
    }

    public int getFailedNum() {
        return failedNum;
    }

    public String getFailedType() {
        String re = "";
        for (Integer i : this.failedErrorType) {
            re += i + " . ";
        }
        return re;
    }

    public HttpHost getHttpHost() {
        return httpHost;
    }

    public int getReuseTimeInterval() {
        return reuseTimeInterval;
    }

    public void setReuseTimeInterval(int reuseTimeInterval) {
        this.reuseTimeInterval = reuseTimeInterval;
        this.canReuseTime = System.nanoTime() + TimeUnit.NANOSECONDS.convert(reuseTimeInterval, TimeUnit.MILLISECONDS);

    }

    @Override
    public long getDelay(TimeUnit unit) {
        return unit.convert(canReuseTime - System.nanoTime(), TimeUnit.NANOSECONDS);
    }

    @Override
    public int compareTo(Delayed o) {
        Proxy that = (Proxy) o;
        return canReuseTime > that.canReuseTime ? 1 : (canReuseTime < that.canReuseTime ? -1 : 0);
    }

    @Override
    public String toString() {
        return toString1();
    }
    private String toString1() {
        String re = String.format("host: %15s-%d >> %5dms >> success: %-3.2f%% >> fail:%d >> borrow: %d", httpHost.getHostName(), httpHost.getPort(), responseTime,
                successNum * 100.0 / borrowNum, failedNum, borrowNum);
        return re;

    }
    public String toHostString() {
        return httpHost == null ? "localhost" : toHostString(httpHost.getHostName(), httpHost.getPort());
    }

    public static String toHostString(String name, int port) {
        return String.format("%s:%d", name, port);
    }

    public void borrowNumIncrement(int increment) {
        this.borrowNum += increment;
    }

    public int getBorrowNum() {
        return borrowNum;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public Long getResponseTime() {
        return responseTime;
    }

    @Override
    public String toKey() {
        return getProxyKey() == null ? "Proxy-" + toHostString() : getProxyKey();
    }

    @Override
    public boolean recover() {
        if (System.currentTimeMillis() - recoveryUpdateTs > recoveryDuration) {
            return ProxyManagerFactory.getProxyManager().releaseProxy(null, this);
        }
        return false;
    }

    public Long getLastBorrowTime() {
        return lastBorrowTime;
    }

    public void setLastUseTime(Long lastUseTime) {
        this.lastUseTime = lastUseTime;
    }

    public int incrementCounter() {
        return counter.incrementAndGet();
    }
    public int decrementCounter() {
        return counter.decrementAndGet();
    }
    public int getCounter() {
        return counter.intValue();
    }


    public static int getProxyMode(SimpleObject context){
        Number num = context == null ? null : context.getNumber(ProxyManager.CONTEXT_PROXY_MODE);
        return num == null ? -1 : num.intValue();
    }
    public static String getSite(SimpleObject context){
        return context == null ? null : context.getString(ProxyManager.CONTEXT_SITE);
    }
    public static Boolean getIsAuthenticate(SimpleObject context){
        Boolean value = context == null ? null : context.getBoolean(ProxyManager.CONTEXT_IS_AUTHENTICATE);
        return value == null ? false : value;
    }

}
