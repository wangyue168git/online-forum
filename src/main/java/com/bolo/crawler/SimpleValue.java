package com.bolo.crawler;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;

/**
 * @Author wangyue
 * @Date 14:55
 */
public class SimpleValue implements Serializable {
    private static final long serialVersionUID = -4205856499095488186L;
    private Object _value;
    public SimpleValue(Object v) {
        this._value = v;
    }
    private Object getValue() {
        return _value;
    }
    public Object get() {
        return getValue();
    }
    public Number getNumber() {
        return (Number) getValue();
    }
    public Double getDouble() {
        return (Double) getValue();
    }
    public Integer getInteger() {
        return (Integer) getValue();
    }
    public String getString() {
        return (String) getValue();
    }
    public String[] getStringArray() {
        return (String[]) getValue();
    }
    public Date getDate() {
        return (Date) getValue();
    }
    public Boolean getBoolean() {
        return (Boolean) getValue();
    }
    public Collection getCollection() {
        return (Collection) getValue();
    }

    public Double convertToDouble(Double defaultValue) {
        if (_value instanceof Number) {
            return ((Number)_value).doubleValue();
        }
        if (_value instanceof String) {
            return null;//StringUtil.convertStrToDouble(_value, defaultValue);
        }
        return defaultValue;
    }
    public String toString() {
        if (_value != null) {
            return _value.toString();
        }
        return "";
    }
}
