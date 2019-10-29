package com.bolo.crawler.entitys;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;

/**
 * @Author wangyue
 * @Date 14:53
 */
public class SimpleObject implements Serializable {

    private static final long serialVersionUID = 8247332428694362912L;
    protected transient Logger logger = LoggerFactory.getLogger(getClass());
    private Map<String, SimpleValue> dataMap;

    public SimpleObject() {
        init();
    }
    public SimpleObject put(String key, Object value) {
        if (value != null) {
            dataMap.put(key, new SimpleValue(value));
        }
        return this;
    }
    public SimpleObject putAll(SimpleObject obj) {
        if (obj != null) {
            dataMap.putAll(obj.dataMap);
        }
        return this;
    }
    public Object get(String key) {
        SimpleValue simpleValue = dataMap.get(key);
        return simpleValue == null ? null : simpleValue;
    }
    public Object getObject(String key) {
        SimpleValue simpleValue = dataMap.get(key);
        return simpleValue == null ? null : simpleValue.get();
    }
    public Number getNumber(String key) {
        SimpleValue simpleValue = dataMap.get(key);
        return simpleValue == null ? null : simpleValue.getNumber();
    }
    public Double getDouble(String key) {
        SimpleValue simpleValue = dataMap.get(key);
        return simpleValue == null ? null : simpleValue.getDouble();
    }
    public Integer getInteger(String key) {
        SimpleValue simpleValue = dataMap.get(key);
        return simpleValue == null ? null : simpleValue.getInteger();
    }
    public String getString(String key) {
        SimpleValue simpleValue = dataMap.get(key);
        return simpleValue == null ? null : simpleValue.getString();
    }
    public String[] getStringArray(String key) {
        SimpleValue simpleValue = dataMap.get(key);
        return simpleValue == null ? null : simpleValue.getStringArray();
    }
    public Date getDate(String key) {
        SimpleValue simpleValue = dataMap.get(key);
        return simpleValue == null ? null : simpleValue.getDate();
    }
    public Boolean getBoolean(String key) {
        SimpleValue simpleValue = dataMap.get(key);
        return simpleValue == null ? null : simpleValue.getBoolean();
    }
    public Collection getCollection(String key) {
        SimpleValue simpleValue = dataMap.get(key);
        return simpleValue == null ? null : simpleValue.getCollection();
    }
    public Collection<String> keySet() {
        return dataMap.keySet();
    }
    public Collection<Map.Entry<String, SimpleValue>> entrySet() {
        return dataMap.entrySet();
    }
    public String getValueAsString(String key) {
        return (String) dataMap.get(key).getString();
		/*boolean newValue;
	      if (newValue instanceof Boolean) {
	          changedValue = newValue; // autoboxing handles this for you
	      } else if (newValue instanceof String) {
	          changedValue = Boolean.parseBoolean(newValue);
	      } else {
	          // handle other object types here, in a similar fashion to above
	      }*/
    }
    public int size() {
        return dataMap.size();
    }
    public void clear() {
        dataMap.clear();
    }
    public String toString() {
        StringBuilder sb = new StringBuilder();
        Collection<Map.Entry<String, SimpleValue>> list = entrySet();
        int i = 0;
        for(Map.Entry<String, SimpleValue> e : list) {
            if (i > 0) {
                sb.append("|");
            }
            sb.append(e.getKey()).append("=").append(e.getValue());
            i ++;
        }
        return sb.toString();
    }
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<String, Object>();
        Collection<Map.Entry<String, SimpleValue>> list = entrySet();
        int i = 0;
        for(Map.Entry<String, SimpleValue> e : list) {
            map.put(e.getKey(), e.getValue().get());
            i ++;
        }
        return map;
    }
    private void init() {
        if (dataMap == null) {
            dataMap = new HashMap();
        }
    }
    public void error(String msg) {

    }

}
