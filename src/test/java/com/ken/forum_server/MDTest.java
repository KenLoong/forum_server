package com.ken.forum_server;

import com.ken.forum_server.pojo.Post;
import com.ken.forum_server.util.MD5Util;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = ForumServerApplication.class)
public class MDTest {

    @Test
    public void T() {
        System.out.println(MD5Util.md5Encryption("5522150Aa#"));
    }

}
