package com.bolo.crawler.interfaceclass;

import com.bolo.crawler.entitys.Request;
import com.bolo.crawler.entitys.SimpleObject;

import java.io.Serializable;

/**
 * @Author wangyue
 * @Date 14:50
 */
public interface ProcessorObserver  {

    String KEY_CONTENT = "ProcessorObserver.content";
    String KEY_REQUEST = "ProcessorObserver.request";
    String KEY_TASK = "ProcessorObserver.task";
    String KEY_HTTPRESPONSE = "ProcessorObserver.httpResponse";
    String KEY_STREAM = "ProcessorObserver.stream";
    String KEY_ERROR = "ProcessorObserver.error";
    String KEY_COOKIES = "ProcessorObserver.Cookies";
    String KEY_RESP_TIME = "ProcessorObserver.Resp.Time";
    String KEY_OBJECT = "ProcessorObserver.obj";
    String KEY_RETRY = "ProcessorObserver.retry";
    String KEY_OBSERVER = "ProcessorObserver.observer";
    String KEY_TIMES = "ProcessorObserver.times";

    void preparedData(SimpleObject context) throws Exception;

    void beforeRequest(SimpleObject context) throws Exception;

    void afterRequest(SimpleObject context) throws Exception;

    void breakRequest(Request req) throws Exception;
    String getClassification();
}
