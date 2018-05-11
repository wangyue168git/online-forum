package com.bolo.crawler.httpclient;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

/**
 * @Author wangyue
 * @Date 15:57
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProxyResponse {
    public static final String STATUS_SUCCESS = "SUCCESS";
    public static final String STATUS_FAILURE = "FAILURE";
    private String ip;
    private int port;
    private int seqNo;
    private String proxyKey;
    private String site;
    private boolean release;
    private String status = STATUS_SUCCESS;
    private String errMsg;
}
