package com.bolo.test.auther;

/**
 * @Author wangyue
 * @Date 16:25
 */
public class AuthException extends RuntimeException {

    private static final long seriaVersionUID = 1364225358754654703L;

    public AuthException(){
        super("权限不足");
    }

    public AuthException(String message){
        super(message);
    }
}
