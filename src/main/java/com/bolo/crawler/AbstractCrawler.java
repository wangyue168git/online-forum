package com.bolo.crawler;

import com.bolo.util.ArrayUtil;
import com.bolo.util.DateUtils;
import com.bolo.util.JSUtil;
import com.bolo.util.RobotUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.http.HttpHost;
import org.apache.http.client.CookieStore;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author wangyue
 * @Date 16:49
 */
@Getter
@Slf4j
public abstract class AbstractCrawler extends StatusTracker{
    public static final int LOGIN_SUC = 1;
    protected Logger logger = LoggerFactory.getLogger("Crawler");
    //临时数据
    protected SimpleObject data = new SimpleObject();
    //和登录状态同时保存的数据
    protected SimpleObject entity = new SimpleObject();
    protected Spider spider;
    private boolean test;
    protected HttpHost httpHost;
    protected long timeMillis = System.currentTimeMillis();
    protected String prefixUrl;
    protected String imgPath;
    @Setter
    protected String certId;

    public AbstractCrawler() {
        httpHost = new HttpHost("aws-proxy.cashbus.com", 7077);
    }
    public long timeMillis() {
        return System.currentTimeMillis();
    }
    public void sleep(long t) {
        try {
            Thread.sleep(t);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    public boolean isTest() {
        return test;
    }
    public void setTest(boolean test) {
        this.test = test;
    }

    public void notifyStatus() {
        if (isNotified()) {
            return;
        }
        spider.addStage();
        publishEvent(EVENT_SAVE_ENTITY);
        super.notifyStatus();
    }

    public static final String EVENT_SAVE_ENTITY = "saveEntity";
    public void publishEvent(String event) {
        spider.publishEvent(event);
    }

    public void finishSpider() {
        spider.setSpiderLastStep(true);
    }

    protected void removeClassification(String clf) {
        spider.addClassificationStatus(clf, Spider.CLASSFICATION_STATUS_REMOVE);
    }
    protected void getUrl(String url, String referer, ProcessorObserver observer) {
        getUrl(url, referer, null, observer);
    }
    protected void getUrl(String url, String referer, Object[] param, ProcessorObserver observer) {
        getUrl(url, referer, param, null, observer, null);
    }
    protected void getUrl(String url, String referer, Object[] param, String[][] headers, ProcessorObserver observer) {
        getUrl(url, referer, param, headers, observer, null);
    }
    protected void getUrl(String url, String referer, Object[] param, String[][] headers, ProcessorObserver observer, SimpleObject context) {
        if (url == null) {
            logger.error("Error : No URL");
        } else {
            Request req = new Request(url.trim());
            //req.setMethod("POST");
            if (referer != null) {
                req.putHeader("Referer", referer);
            }
            if (observer != null) {
                req.addObjservers(observer);
            }

            setRequest(param, headers, req, context);
            spider.addRequest(req);
        }
    }
    protected void postUrl(String url, String referer, String[][] nameValuePairs, ProcessorObserver observer) {
        postUrl(url, referer, null, nameValuePairs, observer);
    }
    protected void postUrl(String url, String referer, Object[] param, String[][] nameValuePairs, ProcessorObserver observer) {
        postUrl(url, referer, param, nameValuePairs, null, observer, null);
    }
    protected void postUrl(String url, String referer, Object[] param, String[][] nameValuePairs, String[][] headers, ProcessorObserver observer) {
        postUrl(url, referer, param, nameValuePairs, headers, observer, null);
    }
    protected void postUrl(String url, String referer, Object[] param, String[][] nameValuePairs, String[][] headers, ProcessorObserver observer, SimpleObject context) {
        if (url == null) {
            logger.error("Error : No URL");
        } else {
            Request req = new Request(url.trim());
            req.setMethod("POST");
            if (referer != null) {
                req.putHeader("Referer", referer);
            }
            if (nameValuePairs != null) {
                req.setNameValuePairs(nameValuePairs);
            }
            if (observer != null) {
                req.addObjservers(observer);
            }

            setRequest(param, headers, req, context);
            spider.addRequest(req);
        }
    }
    private void setRequest(Object[] param, String[][] headers, Request req, SimpleObject context) {
        req.putExtra("sequenceNo", spider.getSequenceNo());
        if (param != null) {
            int len = param.length;
            if (len > 0 && param[0] != null) {
                req.setCharset(param[0].toString());
            }
            if (len > 1 && param[1] != null) {
                req.setPostXml(param[1].toString());
            }
            if (len > 2 && param[2] != null) {
                req.setUseProxy(true);
                req.putExtra(Request.PROXY, param[2]);
            }
            if (len > 3 && param[3] != null) {
                req.setPostContentType(param[3].toString());
            }
            if (len > 4 && param[4] != null) {
                req.setPostContentTypeCharSet(param[4].toString());
            }
        }
        if (headers != null) {
            for(String[] header : headers) {
                req.putHeader(header[0], header[1]);
            }
        }
        if (httpHost != null && !req.isUseProxy()) {
            req.setUseProxy(true);
            req.putExtra(Request.PROXY, httpHost);
        }
        if (context != null) {
            Number num = context.getNumber(Request.KEY_PRIORITY);
            if (num != null) {
                req.setPriority(num.longValue());
            }
            if (context.getObject(Request.STREAM) != null) {
                req.putExtra(Request.STREAM, "true");
            }
            Map<String, Object> map = (Map<String, Object>) context.getObject(Request.EXTRAS);
            if (map != null) {
                req.getExtras().putAll(map);
            }
        }
        req.putExtra(Request.OBJECT, this);
    }
    public void forbidden(SimpleObject context, String desc) {
        if (SpiderManager.getActivateStatistics()) {
            StatisticsSpiderListener ssl = StatisticsSpiderListener.getInstance();
            ssl.reqError(context, desc);
        }
    }
    public Statistics getSiteStatistics() {
        if (SpiderManager.getActivateStatistics()) {
            StatisticsSpiderListener ssl = StatisticsSpiderListener.getInstance();
            return ssl.getSiteStatistics(spider.getSite());
        }
        return null;
    }
    public String saveFileContentHeaders(String url, String referer, String host, String picName, final boolean notifyStatus, String[][] headers) throws Exception {
        return saveFileTest(url, referer, host, picName, headers);
    }

    public String saveFile(String url, String referer, String host, String picName, final boolean notifyStatus, String[][] headers) throws Exception {
        if (isTest()) {
            return saveFileTest(url, referer, host, picName, headers);
        }
        return saveFile2(url, referer, host, picName, notifyStatus, headers);
    }
    public String saveByte(SimpleObject context, String picName, byte[] bs) throws Exception {
        if (isTest()) {
            return saveByteTest(picName, bs);
        }
        final String key = getPickey(picName);
        saveBytes(context, bs, key, picName);
        return key;
    }

    public String saveFile(String url, String referer, String host, String picName, final boolean notifyStatus) throws Exception {
        if (isTest()) {
            return saveFileTest(url, referer, host, picName, null);
        }
        return saveFile2(url, referer, host, picName, notifyStatus, null);
    }
    public String getPickey(String picName) throws Exception {
        final String suffix = (int) (Math.random() * 100000) + "-" + System.currentTimeMillis() + ""; //DateUtils.formatDate(new Date(), "yyyyMMdd");
        return URLEncoder.encode((suffix + "-" + picName).replaceAll("@", "").replaceAll("\\.", ""), "utf-8").replaceAll("%", "");
    }
    public String saveFile2(String url, String referer, String host, String picName, final boolean notifyStatus, String[][] headers) throws Exception {
        //String authcodePath = "";//InfoUtil.getInstance().getInfo("road", "server.full.path");

        //String path1 = suffix;
		/*
		File file2 = new File(path1);
		if (!file2.exists() && !file2.isDirectory()) {
			file2.mkdir();
		}
		*/
        //final String destfilename = path1 + "/"+ picName;
        Request req = new Request(url);
        //req.setMethod("POST");
        if (referer != null) {
            req.putHeader("Referer", referer);
        }
        if (host != null) {
            req.putHeader("Host", host);
        }
        req.putHeader("Accept", "image/png, image/svg+xml, image/*;q=0.8, */*;q=0.5");
        //req.putHeader("Accept", "	text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
        req.putHeader("Accept-Encoding", "gzip, deflate");
        req.putHeader("Accept-Language", "zh-cn,zh;q=0.8,en-us;q=0.5,en;q=0.3");
        req.putHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:31.0) Gecko/20100101 Firefox/31.0");
        if (headers != null){
            for (int i = 0; i < headers.length; i++){
                req.putHeader(headers[i][0], headers[i][1]);
            }
        }
        req.putExtra(Request.STREAM, "true");
        final long sts = System.currentTimeMillis();
        final String key = getPickey(picName);
        //final CountDownLatch latch = new CountDownLatch(1);
        req.addObjservers(new AbstractProcessorObserver() {
            @Override
            public void afterRequest(SimpleObject context) {
                double diff = (System.currentTimeMillis() - sts) / 1000d;
                log.info("---imgUrlTime request img ok time(s) :" + diff);
                try {
                    InputStream is = ContextUtil.getInputStream(context);
                    saveFile(context, is, key, picName);
                    diff = (System.currentTimeMillis() - sts) / 1000d;
                    log.info("---imgUrlTime save img ok time(s) :" + diff);
                    //latch.countDown();
                } catch (Exception e) {
                    logger.error("inputStream", e);
                    //e.printStackTrace();
                } finally {
                    if (notifyStatus) {
                        notifyStatus();
                    }
                }
                //logger.info("---save img end time(s) :" + (System.currentTimeMillis() - sts) / 1000d);
            }


        });
        spider.addRequest(req);
        //latch.await();

        return key;// + InfoUtil.getInstance().getInfo("road", "server.img.auth.code.suffix");
    }

    protected void saveFile(SimpleObject context, InputStream is, String key, String picName) {
        //double diff;
        if (is != null) {
            //FileOutputStream output = new FileOutputStream(destfilename);
            //ByteArrayOutputStream output = new ByteArrayOutputStream();
            try {
                byte[] bs = IOUtils.toByteArray(is);
                Map<String, Object> map = new HashMap<>();
                //map = RobotUtil.getCacheMap(null, key);
                map.put("1", bs);
                RobotUtil.setCacheMap(null, key, map, 6 * 60);
                afterSaveFile(picName, context);
                //IOUtils.copy(is, output);
                //Thread.sleep(100);
            } catch (Exception e) {
                logger.error("read stream", e);
            } finally {
                IOUtils.closeQuietly(is);
                //IOUtils.closeQuietly(output);
            }

					/*PicUpload picUpload = new PicUpload();
                    picUpload.upload(destfilename);*/
        }
    }
    protected void saveBytes(SimpleObject context, byte[] bs, String key, String picName) {
        //double diff;
        if (bs != null) {
            //FileOutputStream output = new FileOutputStream(destfilename);
            //ByteArrayOutputStream output = new ByteArrayOutputStream();
            try {
                Map<String, Object> map = new HashMap<>();
                //map = RobotUtil.getCacheMap(null, key);
                map.put("1", bs);
                RobotUtil.setCacheMap(null, key, map, 6 * 60);
                afterSaveFile(picName, context);
                //IOUtils.copy(is, output);
                //Thread.sleep(100);
            } catch (Exception e) {
                logger.error("saveBytes stream", e);
            } finally {

            }
        }
    }

    protected void afterSaveFile(String fileName, SimpleObject context) {
    }

    public String getTestPicKey(String picName) {
        String authcodePath = imgPath;//InfoUtil.getInstance().getInfo("road", "authcodePath");
        final String suffix = DateUtils.formatDate(new Date(), "MMdd");
        String path1 = authcodePath+suffix;

        File file2 = new File(path1);
        if (!file2.exists() && !file2.isDirectory()) {
            file2.mkdir();
        }
        return suffix + "/"+ picName;
    }
    public String saveFileTest(String url, String referer, String host, String picName, String[][] headers) throws Exception {
        String authcodePath = imgPath;//InfoUtil.getInstance().getInfo("road", "authcodePath");
        final String suffix = DateUtils.formatDate(new Date(), "MMdd");
        String path1 = authcodePath+suffix;

        File file2 = new File(path1);
        if (!file2.exists() && !file2.isDirectory()) {
            file2.mkdir();
        }
        final String destfilename = path1 + "/"+ picName + ".jpg";
        Request req = new Request(url);
        //req.setMethod("POST");
        if (referer != null) {
            req.putHeader("Referer", referer);
        }
        if (host != null) {
            req.putHeader("Host", host);
        }
        req.putHeader("Accept", "image/png, image/svg+xml, image/*;q=0.8, */*;q=0.5");
        //req.putHeader("Accept", "	text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
        req.putHeader("Accept-Encoding", "gzip, deflate");
//		req.putHeader("Cookie", "ASP.NET_SessionId=4tfc32ojcpgu0sofmlytgywb");
        req.putHeader("Accept-Language", "zh-cn,zh;q=0.8,en-us;q=0.5,en;q=0.3");
        req.putHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:31.0) Gecko/20100101 Firefox/31.0");
        if (headers != null){
            for (int i = 0; i < headers.length; i++){
                req.putHeader(headers[i][0], headers[i][1]);
            }
        }
        req.putExtra(Request.STREAM, "true");
        final long sts = System.currentTimeMillis();
        //final CountDownLatch latch = new CountDownLatch(1);
        req.addObjservers(new AbstractProcessorObserver() {
            @Override
            public void afterRequest(SimpleObject context) {
                //com.lkb.debug.DebugUtil.printCookieData(ContextUtil.getCookieStore(context), null);
                try {
                    InputStream is = ContextUtil.getInputStream(context);
                    if (is != null) {
                        saveTestFile(is, destfilename);
                        logger.info("---save img ok time(s) :" + (System.currentTimeMillis() - sts) / 1000d);
						/*PicUpload picUpload = new PicUpload();
						picUpload.upload(destfilename);*/
                    }

                    //latch.countDown();
                } catch (Exception e) {
                    logger.error("inputStream", e);
                    e.printStackTrace();
                } finally {
                }
                //logger.info("---save img end time(s) :" + (System.currentTimeMillis() - sts) / 1000d);
            }
        });
        spider.addRequest(req);
        //latch.await();
        data.put(destfilename, "imgName");
        return suffix + "/"+ picName;
    }
    public String saveByteTest(String picName, byte[] bs) throws Exception {
        String authcodePath = imgPath;//InfoUtil.getInstance().getInfo("road", "authcodePath");
        final String suffix = DateUtils.formatDate(new Date(), "MMdd");
        String path1 = authcodePath+suffix;

        File file2 = new File(path1);
        if (!file2.exists() && !file2.isDirectory()) {
            file2.mkdir();
        }
        final String destfilename = path1 + "/"+ picName + ".jpg";
        FileUtils.writeByteArrayToFile(new File(destfilename), bs);
        //latch.await();
        data.put(destfilename, "imgName");
        return suffix + "/"+ picName;
    }

    protected void saveTestFile(InputStream is, String destfilename) throws FileNotFoundException {
        FileOutputStream output = new FileOutputStream(destfilename);
        //ByteArrayOutputStream output = new ByteArrayOutputStream();
        try {
            IOUtils.copy(is, output);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(is);
            IOUtils.closeQuietly(output);
        }
    }

    public void printCookieData() {
        CookieStore cs = CookieStoreUtil.putContextToCookieStore(null, 1);
//        DebugUtil.printCookieData(cs, null);
    }

    public void printData() {
        printSimpleValue(data);
    }
    public void printEntityData() {
        printSimpleValue(entity);
    }
    public void printSimpleValue(SimpleObject so) {
        Collection<Map.Entry<String, SimpleValue>> entryset = so.entrySet();
        for(Map.Entry<String, SimpleValue> entry : entryset) {
            logger.info(entry.getKey() + " : " + entry.getValue().toString());
        }
    }
    public String unescapeHtml(String text) {
        return StringEscapeUtils.unescapeHtml4(text);
    }
    //
    public static String executeJsFunc(String jsFile, String jsFuncName, Object... args) {
        //String jsPath = test ? jsFile : null;//InfoUtil.getInstance().getInfo("road","tomcatWebappPath")+"/js/" + jsFile;
        return JSUtil.executeResourceJsFunc(jsFuncName, jsFile, args);
    }
    /*public static String executeJsFuncWithEnv(String jsFile, String jsFuncName, Object... args) throws Exception {
        String jsPath = InfoUtil.getInstance().getInfo("road","tomcatWebappPath")+"/js/";
        Object s = JSUtil.executeJsFuncWithEnv(jsFuncName, jsPath + "envjs", jsPath + jsFile, args);
        return s == null ? null : s.toString();
    }*/
    protected String[] toStrArr(String... args) {
        return args;
    }
    protected Object[] toArr(Object... args) {
        return args;
    }
    /**
     * 将二位数组转为一个dwr框架post提交的参数格式
     * @author JerrySun
     * @param pairs
     * @return key1=value1\r\nkey2=value2\r\n...
     */
    protected String joinPairsToPostBodyForDWR(String[][] pairs){
        StringBuffer sb = new StringBuffer();
        for(int i=0;i<pairs.length;i++){
            String key = "";
            String value = "";
            for(int j=0;j<2;j++){
                if(j==0)
                    key = pairs[i][j];
                else if (j==1)
                    value = pairs[i][j];
            }
            sb.append(key).append("=").append(value).append("\r\n");
        }
        return sb.toString();
    }
    public static String valOfElement(Element e, String filter) {
        Elements es1 = e.select(filter);
        return es1.val();
    }


    /**
     * <p>Title: mapToArray</p>
     * <p>Description: 将map转为二维数组</p>
     * @author Jerry Sun
     * @param map
     * @return Object[][]
     */
    protected Object[][] mapToArray(Map map){
        Object[][] array = new String[map.size()][2];

        Object[] keys = map.keySet().toArray();
        Object[] values = map.values().toArray();

        for (int row = 0; row < array.length; row++) {
            array[row][0] = keys[row];
            array[row][1] = values[row];
        }
        return array;
    }

    public void releaseProxy() {
        spider.setReleaseProxy(true);
    }
    public void removeProxy() {
        spider.setRemoveProxy(true);
    }
    public void removeProxy(long recoverTime) {
        spider.setRemoveProxy(true);
        if(recoverTime > 0) {
            spider.setRecoverTime(0);
        }
    }
    public void destory() {
        data.clear();
        entity.clear();
        spider.close();
        spider = null;
    }
    public static int indexOf(Object[] array, Object objectToFind) {
        return ArrayUtil.indexOf(array, objectToFind);
    }
    public static int indexOf(Object[] array, Object objectToFind, int startIndex) {
        return ArrayUtil.indexOf(array, objectToFind, startIndex);
    }
    public void setPrefixUrl(String prefixUrl) {
        this.prefixUrl = prefixUrl;
    }
}
