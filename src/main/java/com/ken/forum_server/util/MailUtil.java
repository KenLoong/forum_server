package com.ken.forum_server.util;

import com.ken.forum_server.pojo.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Component
public class MailUtil {

        private static final Logger logger = LoggerFactory.getLogger(MailUtil.class);

        @Autowired
        private JavaMailSender mailSender;

        @Value("${spring.mail.username}")
        private String from;

//        private String serverId = "47.115.88.155";
        private String serverId = "localhost";

    /**
     *
     * @param to：邮件接受者
     * @param subject：邮件主题
     * @param content:邮件内容
     */
    private void sendMail(String to, String subject, String content) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);
        System.out.println(from);
        helper.setFrom(from);
        helper.setTo(to);
        //设置主题
        helper.setSubject(subject);
        helper.setText(content, true);
        logger.info("发送邮件内容："+content);
        mailSender.send(helper.getMimeMessage());
    }


    /**
     * 发送注册邮件
     * @param to
     * @param subject
     * @param code
     * @param username
     */
    public void registerMail(String to, String subject, String code , String username) throws MessagingException {
        String content = "您已注册ken社区，快来点击连接激活吧！<a href='http://"+serverId+":8089/forum_server/user/active?code="+code+"&username="+username+"'>ken社区</a>，如果不是您注册，请不要点击激活连接";
        sendMail(to,subject,content);
    }

    /**
     * 发送新密码邮件
     * @param to
     * @param subject
     * @param user
     * @throws MessagingException
     */
    public void forgetMail(String to, String subject, User user) throws MessagingException {
        String content = "尊敬的 "+ user.getUsername() + " , 您的新密码是："+user.getPassword();
        sendMail(to,subject,content);
    }

}
