package com.ken.forum_server.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan("com.ken.forum_server.dao")
public class MybatisConfig {

}
