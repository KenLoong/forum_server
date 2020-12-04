package com.ken.forum_server.async;

import com.alibaba.fastjson.JSONObject;
import com.ken.forum_server.pojo.Event;
import com.ken.forum_server.pojo.User;
import com.ken.forum_server.util.ConstantUtil;
import com.ken.forum_server.util.MailUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import java.util.Map;

/**
 * 发送邮件的线程
 */
@Component
@Scope("prototype")
public class EmailTask implements Runnable {

    private Event event;

    @Autowired
    private MailUtil mailUtil;

    public void setEvent(Event event){
        this.event = event;
    }

    /**
     * 注册
     */
    private void register()  {
        Map<String, Object> data = event.getData();
        User user = (User)data.get("user");

        System.out.println(mailUtil);
        //发送邮件
        try {
            mailUtil.registerMail(user.getEmail(),"欢迎来到ken社区",user.getCode(),user.getUsername());
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    /**
     * 忘记密码
     */
    private void forget() {
        Map<String, Object> data = event.getData();
        User user = (User)data.get("user");
        //发送邮件
        try {
            mailUtil.forgetMail(user.getEmail(),"忘记密码",user);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void run() {
       if (ConstantUtil.TOPIC_REGISTER.equals(event.getTopic())){
           register();
       }else if (ConstantUtil.TOPIC_FORGET.equals(event.getTopic())){
           forget();
       }
    }
}
