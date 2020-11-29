package com.ken.forum_server.async;

import com.alibaba.fastjson.JSONObject;
import com.ken.forum_server.pojo.Event;
import com.ken.forum_server.pojo.User;
import com.ken.forum_server.util.MailUtil;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

/**
 * 发送邮件的线程
 */
public class EmailTask implements Runnable {

    private Event event;
    @Autowired
    MailUtil mailUtil;

    public EmailTask(Event event){
        this.event = event;
    }


    @Override
    public void run() {
        Map<String, Object> data = event.getData();
        User user = (User)data.get("user");
        //发送邮件
        mailUtil.sendMail(user.getEmail(),"欢迎来到ken社区",user.getCode(),user.getUsername());
    }
}
