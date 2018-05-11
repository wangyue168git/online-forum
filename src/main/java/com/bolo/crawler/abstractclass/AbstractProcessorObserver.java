package com.bolo.crawler.abstractclass;

import com.bolo.crawler.utils.ContextUtil;
import com.bolo.crawler.entitys.Request;
import com.bolo.crawler.utils.SessionUtil;
import com.bolo.crawler.entitys.SimpleObject;
import com.bolo.crawler.interfaceclass.ProcessorObserver;
import com.bolo.crawler.interfaceclass.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Author wangyue
 * @Date 16:56
 */
public abstract class AbstractProcessorObserver implements ProcessorObserver {
    protected Logger logger = LoggerFactory.getLogger("Observer");

    public AbstractProcessorObserver() {
    }


    public void preparedData(SimpleObject context) throws Exception {
        // 无错误不会往下引用
        if (ContextUtil.getError(context) != null) {
            // SendMailOnTime.sendAllMail("num 1:test\n"+ContextUtil.getError(context).toString());
            Request request = ContextUtil.getRequest(context);
            Task task = ContextUtil.getTask(context);
            if (!task.isNoLogger()) {
                logger.error(request.getMethod() == null ? "GET" : request.getMethod() + " page "  + request.getUrl(), ContextUtil.getError(context));
                logger.error("abstract observer Response Text:" + org.apache.commons.lang3.StringUtils.left(ContextUtil.getContent(context), 600));
            }
            if (SessionUtil.getObject(SessionUtil.CURRENT_BUSINESS_KEY) != null) {
                // add code here to email
                logger.error("abstract observer preparedData", ContextUtil.getError(context));
                // SendMailOnTime.sendAllMail("num 2:test\n"+ContextUtil.getError(context).toString());
                // WarningUtil.warning("admin", "110", "no-type", , t,
                // "justTest");
            }
        }
    }

    public void beforeRequest(SimpleObject context) throws Exception {
        // System.out.println(ContextUtil.getError(context));
    }

    public void afterRequest(SimpleObject context) throws Exception {

    }
    public void breakRequest(Request request) throws Exception {

    }
    public String getClassification() {
        return null;
    }
}
