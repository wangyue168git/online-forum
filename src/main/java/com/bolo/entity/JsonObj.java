package com.bolo.entity;

import java.util.List;
import java.util.Map;

public class JsonObj<T>{

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    int code;
    String msg;

    public T getMap() {
        return map;
    }

    public void setMap(T map) {
        this.map = map;
    }

    T map;
}
