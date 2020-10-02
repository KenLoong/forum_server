package com.ken.forum_server;

import com.ken.forum_server.dao.PostDao;
import com.ken.forum_server.pojo.Post;
import com.ken.forum_server.util.MailUtil;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;
import java.util.Date;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = ForumServerApplication.class)
public class MailTests {

    @Autowired
    private MailUtil mailUtil;

    @Autowired
    private PostDao postDao;


    @Test
    public void testTextMail() {
//        mailUtil.sendSimpleMail("1067408710@qq.com","开会通知","ssssssdddd");
//        mailUtil.sendMail("1067408710@qq.com","开会通知","ssssssdddd");
    }


    @Test
    public void T() {
        Post post = new Post();
        post.setScore(0);
        post.setCommentCount(0);
        post.setType(0);
        post.setTag(1);
//        post.setCreateTime(LocalDateTime.now());
        post.setUserId(3);
        for (int i = 50; i < 100; i++) {
            post.setTitle("推文"+i);
            post.setContent("内容"+i);
            postDao.addPost(post);

        }
    }
}