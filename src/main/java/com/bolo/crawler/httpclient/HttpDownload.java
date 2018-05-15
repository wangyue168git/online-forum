package com.bolo.crawler.httpclient;

import com.bolo.crawler.abstractclass.AbstractDownloader;
import com.bolo.crawler.entitys.Proxy;
import com.bolo.crawler.entitys.Request;
import com.bolo.crawler.entitys.SimpleObject;
import com.bolo.crawler.entitys.Site;
import com.bolo.crawler.interfaceclass.Downloader;
import com.bolo.crawler.interfaceclass.ProcessorObserver;
import com.bolo.crawler.interfaceclass.Task;
import com.bolo.crawler.utils.ContextUtil;
import com.bolo.crawler.utils.CookieStoreUtil;
import com.bolo.test.reqlimit.HttpRequestUtil;
import com.bolo.util.UrlUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.*;
import org.apache.http.client.CookieStore;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;

import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.Args;
import org.apache.http.util.ByteArrayBuffer;
import org.apache.http.util.CharArrayBuffer;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author wangyue
 * @Date 14:45
 */
public class HttpDownload extends AbstractDownloader {

    private final Map<String,CloseableHttpClient> httpClients = new ConcurrentHashMap<>(3);
    private HttpClientGenerator httpClientGenerator;
    private int thread;


    private CloseableHttpClient getHttpClient(Task task, Request request)
    {
        Site site = task.getSite();
        if (httpClientGenerator == null){
            httpClientGenerator = new HttpClientGenerator(site);
            if (thread > 0){
                httpClientGenerator.setPoolSize(thread);
            }
        }
        if (site == null){
            return httpClientGenerator.getClient(null,request);
        }
        CloseableHttpClient httpClient = null;
        String domain = site.getDomain() == null ? "commomClient" : site.getDomain();
        Map<String,Object> extra = request == null ? new HashMap<>() : request.getExtras();
        if (Request.REFRESH_HTTP_CLIENT_TRUE.equals(extra.get(Request.REFRESH_HTTP_CLIENT))){
            httpClient = httpClientGenerator.getClient(site,request);
            httpClients.put(domain,httpClient);
        }else {
            httpClient = httpClients.get(domain);
            if (httpClient == null){
                httpClient = httpClientGenerator.getClient(site,request);
                httpClients.put(domain,httpClient);
            }
        }
        return httpClient;
    }

    private HttpContext getHttpContext(Task task,Request request){
        Site site = task.getSite();
        HttpContext context = HttpClientContext.create();
        if(site != null && site.isSocketProxy()) {
            if (request != null && request.isUseProxy()) {
                HttpHost hh = (HttpHost) request.getExtra(Request.PROXY);
                if(hh != null) {
                    InetSocketAddress socketAddress = new InetSocketAddress(hh.getHostName(), hh.getPort());
                    context.setAttribute(DefaultConnectionSocketFactory.SOCKS_ADDRESS, socketAddress);
                    context.setAttribute(DefaultConnectionSocketFactory.SOCKS_PROXY, "true");
                }
            }
        }
        return  context;
    }

    @Override
    public void download(Request request, Task task) {
        download(request, task, 1);
    }

    private void download(Request request, Task task, int times) {
        Site site = null;
        if (task != null) {
            site = task.getSite();
        }
        SimpleObject context = new SimpleObject();
        context.put(ProcessorObserver.KEY_REQUEST, request);
        context.put(ProcessorObserver.KEY_TIMES, times);
        context.put(ProcessorObserver.KEY_TASK, task);

    Set<Integer> acceptStatCode;
    String charset = null;
    Map<String, String> headers = null;
        if (site != null) {
        acceptStatCode = site.getAcceptStatCode();
        charset = site.getCharset();
        headers = site.getHeaders();
    } else {
        acceptStatCode = Site.DEFAULT_STATUS_CODE_SET;//Sets.newHashSet(200);
    }
        request.notifyObserver(1, context);
        if (!task.isNoLogger()) {
        logger.info(String.format("%s %s page %s", request.getExtra("sequenceNo"), request.getMethod() == null ? "GET" : request.getMethod(), request.getUrl()));
    }

    CloseableHttpResponse httpResponse = null;
    int statusCode = 0;
    //HttpHost host = null;
    int pcode = Proxy.SUCCESS;
    Exception error = null;
    CloseableHttpClient httpClient = null;
    boolean retry = false;
        try {
        HttpUriRequest httpUriRequest = getHttpUriRequest(request, site, headers);
        HttpContext hc = getHttpContext(task, request);;//new BasicHttpContext();
        httpClient = getHttpClient(task, request);
        long st = System.currentTimeMillis();
        httpResponse = httpClient.execute(httpUriRequest, hc);
        long d = System.currentTimeMillis() - st;
        context.put(ProcessorObserver.KEY_RESP_TIME, d);
        StatusLine statusLine = httpResponse.getStatusLine();
        statusCode = statusLine.getStatusCode();

        request.putExtra(Request.STATUS_CODE, statusCode);
        request.putExtra(Request.RESPONSE_TIME, d);
        CookieStore cookieStore = (CookieStore) hc.getAttribute(HttpClientContext.COOKIE_STORE);
        CookieStoreUtil.addCookieStoreToContext(cookieStore, 1);
        context.put(ProcessorObserver.KEY_HTTPRESPONSE, httpResponse);
        context.put(ProcessorObserver.KEY_COOKIES, cookieStore);
        if (statusAccept(acceptStatCode, statusCode)) {
            handleResponse(request, charset, httpResponse, task, context);
            onSuccess(request);
            retry = ContextUtil.isRetry(context);
            //return page;
        } else if (HttpRequestUtil.acceptCode(statusCode)){
            request.notifyObserver(2, context);
            retry = ContextUtil.isRetry(context);
            //logger.warn("code error " + statusCode + "\t" + request.getUrl());
            //return;
        } else {
            logger.warn(String.format("version:%s statusCode:%d reason:%s " , statusLine.getProtocolVersion().toString(), statusCode, statusLine.getReasonPhrase()));
            retry = true;
        }

        sleep(sleepTime);
    } catch (ConnectTimeoutException e) {
        pcode = Proxy.ERROR_PROXY;
        error = e;
        retry = true;
    } catch (HttpHostConnectException e) {
        pcode = Proxy.ERROR_PROXY;
        error = e;
        retry = true;
    } catch (SocketTimeoutException e) {
        pcode = Proxy.ERROR_404;
        error = e;
        retry = true;
    } catch (SocketException e) {
        pcode = Proxy.ERROR_PROXY;
        error = e;
        retry = true;
    } catch (IOException e) {
        pcode = Proxy.ERROR_403;
        error = e;
        retry = true;
    } catch (Exception e) {
        pcode = Proxy.ERROR_PROXY;
        error = e;
        retry = true;
        //return;
    } finally {
        request.putExtra(Request.STATUS_CODE, statusCode);
        if (retry) {
            if (site.getCycleRetryTimes() > 0 && site.getCycleRetryTimes() >= times ) {

            } else {
                if (error == null) {
                    error = new Exception("retry to many times, but all failed, give up!!!");
                }
                context.put(ProcessorObserver.KEY_ERROR, error);
                request.notifyObserver(3, context);
            }
        } else {
            request.notifyObserver(3, context);
        }
        try {
            if (httpResponse != null) {
                EntityUtils.consume(httpResponse.getEntity());
            }
        } catch (Exception e) {
            logger.warn("close response fail", e);
        }
        request.putExtra(Request.PROXY_STATUS, pcode);

    }
        if (retry) {
        onError(request, task, times, context, error);
    }
}

    private void onError(Request request, Task task, int times, SimpleObject context, Exception e) {
        Site site = task.getSite();
        if (site.getCycleRetryTimes() > 0 && site.getCycleRetryTimes() >= times ) {
            if (!task.isNoLogger()) {
                logger.warn("page " + request.getUrl() + " error times>" + times);
            }
            sleep(500 * (times - 1));
            download(request, task, ++times);
            //return addToCycleRetry(request, site);
        } else {
            if (!task.isNoLogger()) {
                logger.warn("page " + request.getUrl() + " error", e);
            }
            //context.put(ProcessorObserver.KEY_ERROR, e);
        }
    }

    public Downloader setThread(int thread) {
        this.thread = thread;
        return this;
    }

    private boolean statusAccept(Set<Integer> acceptStatCode, int statusCode) {
        return acceptStatCode.contains(statusCode);
    }

    private HttpUriRequest getHttpUriRequest(Request request, Site site, Map<String, String> headers) {
        RequestBuilder requestBuilder = selectRequestMethod(request).setUri(request.getUrl());
        addHeaders(headers, requestBuilder);
        //logger.warn("add request headers {}", request.getHeaders());
        addHeaders(request.getHeaders(), requestBuilder);
        RequestConfig.Builder requestConfigBuilder = RequestConfig.custom()
                .setConnectionRequestTimeout(site.getTimeOut())
                .setSocketTimeout(site.getTimeOut())
                .setConnectTimeout(site.getTimeOut())
                .setRedirectsEnabled(site.isFollowRedirect())
                .setCookieSpec(CookieSpecs.BEST_MATCH);
        if (StringUtils.isNotBlank(site.getCookieSpec())) {
            requestConfigBuilder.setCookieSpec(site.getCookieSpec());
        }
        final Boolean extraRedirect = (Boolean) request.getExtra("redirectsEnabled");
        if (extraRedirect != null) {
            requestConfigBuilder.setRedirectsEnabled(extraRedirect);
        }
        requestBuilder.setConfig(requestConfigBuilder.build());
        return requestBuilder.build();
    }

    private void addHeaders(Map<String, String> headers,
                            RequestBuilder requestBuilder) {
        if (headers != null) {
            for (Map.Entry<String, String> headerEntry : headers.entrySet()) {
                requestBuilder.addHeader(headerEntry.getKey(), headerEntry.getValue());
            }
        }
    }

    private RequestBuilder selectRequestMethod(Request request) {
        String method = request.getMethod();
        if (method == null || method.equalsIgnoreCase(HttpConstant.Method.GET)) {
            //default get
            return RequestBuilder.get();
        } else if (method.equalsIgnoreCase(HttpConstant.Method.POST)) {
            RequestBuilder requestBuilder = RequestBuilder.post();
            boolean notBlankCharSet = StringUtils.isNotBlank(request.getPostContentTypeCharSet());
            NameValuePair[] nameValuePair = (NameValuePair[]) request.getExtra("nameValuePair");
            if (!notBlankCharSet && nameValuePair != null && nameValuePair.length > 0) {
                requestBuilder.addParameters(nameValuePair);
            }
            if (StringUtils.isNotBlank(request.getPostXml())) {
                StringEntity se = new StringEntity(request.getPostXml(), HTTP.UTF_8);
                if (request.getPostContentType() != null) {
                    se.setContentType(request.getPostContentType());
                } else {
                    se.setContentType("text/xml");
                }
                requestBuilder.setEntity(se);
            } else if (notBlankCharSet){
                try {
                    requestBuilder.setEntity(new UrlEncodedFormEntity(Arrays.asList(nameValuePair), request.getPostContentTypeCharSet()));
                    //requestBuilder.getParameters().clear();
                } catch (Exception e) {
                    logger.error("page " + request.getUrl() + " error", e);
                }
            }
            return requestBuilder;
        } else if (method.equalsIgnoreCase(HttpConstant.Method.HEAD)) {
            return RequestBuilder.head();
        } else if (method.equalsIgnoreCase(HttpConstant.Method.PUT)) {
            return RequestBuilder.put();
        } else if (method.equalsIgnoreCase(HttpConstant.Method.DELETE)) {
            return RequestBuilder.delete();
        } else if (method.equalsIgnoreCase(HttpConstant.Method.TRACE)) {
            return RequestBuilder.trace();
        }
        throw new IllegalArgumentException("Illegal HTTP Method " + method);
    }

    private void handleResponse(Request request, String charset, HttpResponse httpResponse, Task task, SimpleObject context) throws IOException {
        if (request.getExtra(Request.STREAM) != null) {
            HttpEntity entity = httpResponse.getEntity();
            InputStream input = entity.getContent();
        	/*if (entity.isStreaming()) {
        	}*/
            context.put(ProcessorObserver.KEY_STREAM, input);
        } else {
            if (request.getCharset() != null) {
                charset = request.getCharset();
            }
            String content = getContent(charset, httpResponse);
            context.put(ProcessorObserver.KEY_CONTENT, content);
        }
        request.notifyObserver(2, context);
    }

    private String getContent(String charset, HttpResponse httpResponse) throws IOException {
        if (charset == null) {
            byte[] contentBytes = toByteArray(httpResponse.getEntity());
            String htmlCharset = charset != null ? charset : getHtmlCharset(httpResponse, contentBytes);
            if (htmlCharset != null) {
                return new String(contentBytes, htmlCharset);
            } else {
                logger.warn(String.format("Charset autodetect failed, use %1$s as charset. Please specify charset in Site.setCharset()", Charset.defaultCharset()));
                return new String(contentBytes);
            }
        } else {
            return toString(httpResponse.getEntity(), charset);
        }
    }
    private byte[] toByteArray(final HttpEntity entity) throws IOException {
        Args.notNull(entity, "Entity");
        final InputStream instream = entity.getContent();
        if (instream == null) {
            return null;
        }
        try {
            Args.check(entity.getContentLength() <= Integer.MAX_VALUE,
                    "HTTP entity too large to be buffered in memory");
            int i = (int)entity.getContentLength();
            if (i < 0) {
                i = 4096;
            }
            final ByteArrayBuffer buffer = new ByteArrayBuffer(i);
            final byte[] tmp = new byte[4096];
            int l;
            try {
                while((l = instream.read(tmp)) != -1) {
                    buffer.append(tmp, 0, l);
                }
            } catch (Exception e) {
                logger.error("error", e);
            }
            return buffer.toByteArray();
        } finally {
            instream.close();
        }
    }
    private String toString(final HttpEntity entity, final String charset) throws IOException, ParseException {
        Args.notNull(entity, "Entity");
        final InputStream instream = entity.getContent();
        if (instream == null) {
            return null;
        }
        try {
            Args.check(entity.getContentLength() <= Integer.MAX_VALUE,
                    "HTTP entity too large to be buffered in memory");
            int i = (int)entity.getContentLength();
            if (i < 0) {
                i = 4096;
            }

            final Reader reader = new InputStreamReader(instream, charset);
            final CharArrayBuffer buffer = new CharArrayBuffer(i);
            final char[] tmp = new char[1024];
            int l;
            try{
                while ((l = reader.read(tmp)) != -1) {
                    buffer.append(tmp, 0, l);
                }
            } catch (EOFException e) {
                logger.error("error", e);
            }
            return buffer.toString();
        } finally {
            instream.close();
        }
    }
    private String getHtmlCharset(HttpResponse httpResponse, byte[] contentBytes) throws IOException {
        String charset;
        if (httpResponse.getEntity().getContentType() == null) {
            return "utf-8";
        }
        // charset
        // 1、encoding in http header Content-Type
        String value = httpResponse.getEntity().getContentType().getValue();
        charset = UrlUtil.getCharset(value);
        if (StringUtils.isNotBlank(charset)) {
            logger.debug(String.format("Auto get charset: %1$s", charset));
            return charset;
        }
        // use default charset to decode first time
        Charset defaultCharset = Charset.defaultCharset();
        String content = new String(contentBytes, defaultCharset.name());
        // 2、charset in meta
        if (StringUtils.isNotEmpty(content)) {
            Document document = Jsoup.parse(content);
            Elements links = document.select("meta");
            for (Element link : links) {
                // 2.1、html4.01 <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
                String metaContent = link.attr("content");
                String metaCharset = link.attr("charset");
                if (metaContent.indexOf("charset") != -1) {
                    metaContent = metaContent.substring(metaContent.indexOf("charset"), metaContent.length());
                    charset = metaContent.split("=")[1];
                    break;
                }
                // 2.2、html5 <meta charset="UTF-8" />
                else if (StringUtils.isNotEmpty(metaCharset)) {
                    charset = metaCharset;
                    break;
                }
            }
        }
        logger.debug(String.format("Auto get charset: %1$s", charset));
        // 3、todo use tools as cpdetector for content decode
        return charset;
    }
    protected void onSuccess(Request request) {
    }
    public void close() {
        if (httpClientGenerator != null) {
            httpClientGenerator.close();
        }
    }
}
