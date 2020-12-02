package com.ken.forum_server.async;

import com.ken.forum_server.pojo.Event;
import com.ken.forum_server.util.ConstantUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 异步处理任务
 */
public class EventHandler {

    //不允许有实例
    private EventHandler(){}

    private static ExecutorService executorService;

    private static Set<String> messageTopics = new HashSet<>();
    private static Set<String> esTopics = new HashSet<>();
    private static Set<String> emailTopics = new HashSet<>();

    static {
        executorService = Executors.newFixedThreadPool(4);
        //通知任务
        messageTopics.add(ConstantUtil.TOPIC_COMMENT);
        messageTopics.add(ConstantUtil.TOPIC_LIKE);
        messageTopics.add(ConstantUtil.TOPIC_FOLLOW);
        //操作es库的任务
        esTopics.add(ConstantUtil.TOPIC_PUBLISH);
        esTopics.add(ConstantUtil.TOPIC_DELETE);
        esTopics.add(ConstantUtil.TOPIC_RESET_ES);
        //发送邮件的任务
        emailTopics.add(ConstantUtil.TOPIC_REGISTER);
        emailTopics.add(ConstantUtil.TOPIC_FORGET);

    }

    public static void handleTask(Event event){
        if (messageTopics.contains(event.getTopic())){
            //通知任务
            executorService.submit(new MessageTask(event));
        }else if (esTopics.contains(event.getTopic())){
            //有关es库的任务
            executorService.submit(new EsTask(event));
        }else if (emailTopics.contains(event.getTopic())){
            //邮件任务
            executorService.execute(new EmailTask(event));
        }
    }


}
