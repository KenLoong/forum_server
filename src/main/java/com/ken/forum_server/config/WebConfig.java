package com.ken.forum_server.config;

import com.ken.forum_server.interceptor.DataInterceptor;
import com.ken.forum_server.interceptor.TokenInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 网络配置
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private DataInterceptor dataInterceptor;

    /**
     * 解决跨域问题
     * @param registry
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods("GET", "HEAD", "POST", "PUT", "DELETE", "OPTIONS")
                .allowCredentials(true)
                .maxAge(3600)
                .allowedHeaders("*");
    }


    //构建bean对象，不如会出现一些对象无法初始化的情况，如userserver
    @Bean
    public HandlerInterceptor getTokenInterceptor(){
        return new TokenInterceptor();
    }


    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //注册拦截器，拦截所有访问路径
        registry.addInterceptor(getTokenInterceptor()).addPathPatterns("/**");
//        registry.addInterceptor(dataInterceptor).addPathPatterns("/**");
    }


}
