package com.ken.forum_server.config;

import com.ken.forum_server.interceptor.DataInterceptor;
import com.ken.forum_server.interceptor.TokenInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;
import java.util.List;

/**
 * 网络配置
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private DataInterceptor dataInterceptor;

    private static final List<String> EXCLUDE_PATH = Arrays.asList("/", "css/**", "js/**", "img/**","/photo", "json/**", "fonts/**","/*.html");


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
        registry.addInterceptor(getTokenInterceptor()).
                addPathPatterns("/**").
                excludePathPatterns(EXCLUDE_PATH);
//        registry.addInterceptor(dataInterceptor).addPathPatterns("/**");

    }

/*WebMvcAutoConfiguration类自动为我们注册了如下目录为静态资源目录，也就是说直接可访问到资源的目录。

registry.addResourceHandler("/**").addResourceLocations(
  "classpath:/META-INF/resources/", "classpath:/resources/",
      "classpath:/static/", "classpath:/public/", "/")
  // 设置缓存时间
  .setCachePeriod(cachePeriod));

优先级从上到下:
classpath:/META-INF/resources/
classpath:/resources/
classpath:/static/
classpath:/public/
/：当前项目的根路径

*/


    //静态资源映射
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/static/**");
    }
}
