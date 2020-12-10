package com.ken.forum_server.config;

import com.ken.forum_server.chat.ChatEndpoint;
import com.ken.forum_server.pojo.Message;
import com.ken.forum_server.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

@Configuration
public class WebSocketConfig {

    @Bean
    //注入ServerEndpointExporter，自动注册使用@ServerEndpoint注解的
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }

    //解决websocket注入问题
    @Autowired
    public void setMessageService(MessageService messageService){
        ChatEndpoint.messageService = messageService;
    }
}
