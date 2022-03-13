package com.ken.forum_server;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

import javax.annotation.PostConstruct;

/**
 * 启动项目条件：    mysql
 *                  redis
 *                  elasticsearch
 *
 */
@EnableAsync
@SpringBootApplication
@MapperScan(basePackages = "com.ken.forum_server.dao")
public class ForumServerApplication {

    @PostConstruct
    public void init() {
        // 解决netty启动冲突问题
        // see Netty4Utils.setAvailableProcessors()
        System.setProperty("es.set.netty.runtime.available.processors", "false");
    }

    public static void main(String[] args) {
        SpringApplication.run(ForumServerApplication.class, args);
    }

}
