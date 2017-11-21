package com.bolo.test.reqlimit;


/**
 * @Author wangyue
 * @Date 14:43
 */
public class RequestLimitException extends RuntimeException{

    private static final long seriaVersionUID = 1364225358754654702L;

    public RequestLimitException(){
        super("HTTP请求超出设定的限制");
    }

    public RequestLimitException(String message){
        super(message);
    }
}
