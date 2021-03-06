//package com.ken.forum_server.event;
//
//import com.alibaba.fastjson.JSONObject;
//import com.ken.forum_server.dao.PostDao;
//import com.ken.forum_server.pojo.Event;
//import com.ken.forum_server.pojo.Message;
//import com.ken.forum_server.pojo.Post;
//import com.ken.forum_server.pojo.User;
//import com.ken.forum_server.service.ElasticSearchService;
//import com.ken.forum_server.service.MessageService;
//import com.ken.forum_server.service.PostService;
//import com.ken.forum_server.util.MailUtil;
//import org.apache.kafka.clients.consumer.ConsumerRecord;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.kafka.annotation.KafkaListener;
//import org.springframework.stereotype.Component;
//
//import java.util.Date;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//import static com.ken.forum_server.util.ConstantUtil.*;
//
//@Component
//public class EventConsumer  {
//
//    private static final Logger logger = LoggerFactory.getLogger(EventConsumer.class);
//
//    @Autowired
//    private MessageService messageService;
//    @Autowired
//    private PostService postService;
//    @Autowired
//    private PostDao postDao;
//    @Autowired
//    private ElasticSearchService elasticSearchService;
//    @Autowired
//    MailUtil mailUtil;
//
//    //监听主题
//    @KafkaListener(topics = {TOPIC_COMMENT, TOPIC_LIKE, TOPIC_FOLLOW})
//    public void handleCommentMessage(ConsumerRecord record) {
//
//        if (record == null || record.value() == null) {
//            logger.error("消息的内容为空!");
//            return;
//        }
//
//        Event event = JSONObject.parseObject(record.value().toString(), Event.class);
//        if (event == null) {
//            logger.error("消息格式错误!");
//            return;
//        }
//        logger.info("消费主题："+event.getTopic());
//
//        // 发送站内通知
//        Message message = new Message();
//        message.setFromId(SYSTEM_USER_ID);
//        message.setToId(event.getEntityUserId());
//        message.setConversationId(event.getTopic());
//        message.setCreateTime(new Date());
//
//        //通知内容
//        Map<String, Object> content = new HashMap<>();
//        content.put("userId", event.getUserId());
//        content.put("entityType", event.getEntityType());
//        content.put("entityId", event.getEntityId());
//
//        if (!event.getData().isEmpty()) {
//            for (Map.Entry<String, Object> entry : event.getData().entrySet()) {
//                content.put(entry.getKey(), entry.getValue());
//            }
//        }
//
//        message.setContent(JSONObject.toJSONString(content));
//        //存入数据库
//        messageService.addMessage(message);
//    }
//
//    // 消费发帖事件
//    @KafkaListener(topics = {TOPIC_PUBLISH})
//    public void handlePublishMessage(ConsumerRecord record) {
//        if (record == null || record.value() == null) {
//            logger.error("消息的内容为空!");
//            return;
//        }
//
//        Event event = JSONObject.parseObject(record.value().toString(), Event.class);
//        if (event == null) {
//            logger.error("消息格式错误!");
//            return;
//        }
//
//        logger.info("消费主题："+event.getTopic());
//
//        Post post = postService.findPostById(event.getEntityId());
//        //更新es库中的帖子信息
//        elasticSearchService.savePost(post);
//    }
//
//    // 消费删帖事件
//    @KafkaListener(topics = {TOPIC_DELETE})
//    public void handleDeleteMessage(ConsumerRecord record) {
//        if (record == null || record.value() == null) {
//            logger.error("消息的内容为空!");
//            return;
//        }
//
//        Event event = JSONObject.parseObject(record.value().toString(), Event.class);
//        if (event == null) {
//            logger.error("消息格式错误!");
//            return;
//        }
//
//        elasticSearchService.deletePost(event.getEntityId());
//        postService.deteteById(event.getEntityId());
//    }
//
//    // 消费es库重新录入事件
//    @KafkaListener(topics = {TOPIC_RESET_ES})
//    public void handleResetEs(ConsumerRecord record) {
//        if (record == null || record.value() == null) {
//            logger.error("消息的内容为空!");
//            return;
//        }
//
//        Event event = JSONObject.parseObject(record.value().toString(), Event.class);
//        if (event == null) {
//            logger.error("消息格式错误!");
//            return;
//        }
//
//        int total = postDao.selectUserPostsCount(event.getEntityId());
//        for (int i = 0; i <= total / 5; i++) {
//            int offset = i * 5;
//            List<Post> posts = postDao.selectPosts(event.getEntityId(), offset, 5);
//            elasticSearchService.saveAllPost(posts);
//        }
//
//    }
//
//    // 消费注册事件
//    @KafkaListener(topics = {TOPIC_REGISTER})
//    public void handleRegister(ConsumerRecord record) {
//        if (record == null || record.value() == null) {
//            logger.error("消息的内容为空!");
//            return;
//        }
//
//        Event event = JSONObject.parseObject(record.value().toString(), Event.class);
//        if (event == null) {
//            logger.error("消息格式错误!");
//            return;
//        }
//        Map<String, Object> data = event.getData();
//        User user = (User)data.get("user");
//        //发送邮件
//        mailUtil.sendMail(user.getEmail(),"欢迎来到ken社区",user.getCode(),user.getUsername());
//    }
//}