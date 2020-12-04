package com.ken.forum_server.async;

import com.alibaba.fastjson.JSONObject;
import com.ken.forum_server.dao.PostDao;
import com.ken.forum_server.pojo.Event;
import com.ken.forum_server.pojo.Post;
import com.ken.forum_server.service.ElasticSearchService;
import com.ken.forum_server.service.MessageService;
import com.ken.forum_server.service.PostService;
import com.ken.forum_server.util.ConstantUtil;
import com.ken.forum_server.util.MailUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 操作es索引库的线程
 */
@Component
@Scope("prototype")
public class EsTask implements Runnable {

    private Event event;
    private static final Logger logger = LoggerFactory.getLogger(EsTask.class);

    @Autowired
    private PostService postService;
    @Autowired
    private ElasticSearchService elasticSearchService;
    @Autowired
    private PostDao postDao;

    public void setEvent(Event event){
        this.event = event;
    }

    private void updateEs(){

        //更新数据库的帖子
        Post post = postService.findPostById(event.getEntityId());
        //更新es库中的帖子信息
        elasticSearchService.savePost(post);
    }


    private void deleteEs(){
        //删除ES库中的帖子
        elasticSearchService.deletePost(event.getEntityId());
        //删除数据库的帖子
        postService.deteteById(event.getEntityId());
    }

    private void resetEs(){

        int total = postDao.selectUserPostsCount(event.getEntityId());
        for (int i = 0; i <= total / 5; i++) {
            int offset = i * 5;
            List<Post> posts = postDao.selectPosts(event.getEntityId(), offset, 5);
            elasticSearchService.saveAllPost(posts);
        }
    }


    @Override
    public void run() {

        if (event == null) {
            logger.error("任务为空");
            return;
        }

        logger.info("es索引库消费任务："+event.getTopic());
        switch (event.getTopic()){
            case ConstantUtil.TOPIC_PUBLISH : updateEs();break;
            case ConstantUtil.TOPIC_DELETE : deleteEs();break;
            case ConstantUtil.TOPIC_RESET_ES : resetEs();break;
        }

    }
}
