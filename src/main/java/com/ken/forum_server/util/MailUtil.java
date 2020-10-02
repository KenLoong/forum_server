package com.ken.forum_server.util;

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

        private String serverId = "47.115.88.155";

    /**
     *
     * @param to
     * @param subject
     * @param code:用户的激活码
     */
        public void sendMail(String to, String subject, String code , String username) {
            try {
                MimeMessage message = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(message);
                System.out.println(from);
                helper.setFrom(from);
                helper.setTo(to);
                helper.setSubject(subject);
                String content = "您已注册ken社区，快来点击连接激活吧！<a href='http://"+serverId+":8089/forum_server/user/active?code="+code+"&username="+username+"'>ken社区</a>，如果不是您注册，请不要点击激活连接";
                helper.setText(content, true);
                mailSender.send(helper.getMimeMessage());
            } catch (MessagingException e) {
                logger.error("发送邮件失败:" + e.getMessage());
            }
    }


}
