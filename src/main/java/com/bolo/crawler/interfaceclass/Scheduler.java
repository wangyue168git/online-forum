package com.bolo.crawler.interfaceclass;

import com.bolo.crawler.entitys.Request;
import javafx.concurrent.Task;

/**
 * @Author wangyue
 * @Date 10:34
 */
public interface Scheduler {
    public void push(Request request, Task task);

    public Request poll(Task task);
}
