package com.ken.forum_server.async;

import com.alibaba.fastjson.JSONObject;
import com.ken.forum_server.pojo.Event;
import com.ken.forum_server.pojo.Message;
import com.ken.forum_server.service.MessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.ken.forum_server.util.ConstantUtil.SYSTEM_USER_ID;

/**
 * 发送通知的线程
 */
public class MessageTask implements Runnable {

    private Event event;
    private static final Logger logger = LoggerFactory.getLogger(MessageTask.class);

    @Autowired
    private MessageService messageService;

    public MessageTask(Event event){
        this.event = event;
    }

    @Override
    public void run() {
        logger.info("任务主题："+event.getTopic());

        // 发送站内通知
        Message message = new Message();
        message.setFromId(SYSTEM_USER_ID);
        message.setToId(event.getEntityUserId());
        message.setConversationId(event.getTopic());
        message.setCreateTime(new Date());

        //通知内容
        Map<String, Object> content = new HashMap<>();
        content.put("userId", event.getUserId());
        content.put("entityType", event.getEntityType());
        content.put("entityId", event.getEntityId());

        if (!event.getData().isEmpty()) {
            for (Map.Entry<String, Object> entry : event.getData().entrySet()) {
                content.put(entry.getKey(), entry.getValue());
            }
        }

        message.setContent(JSONObject.toJSONString(content));
        //存入数据库
        messageService.addMessage(message);
    }
}
