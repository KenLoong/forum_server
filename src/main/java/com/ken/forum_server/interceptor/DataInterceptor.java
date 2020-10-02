package com.ken.forum_server.interceptor;

import com.ken.forum_server.controller.BaseController;
import com.ken.forum_server.pojo.User;
import com.ken.forum_server.service.DataService;
import com.ken.forum_server.service.UserService;
import com.ken.forum_server.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class DataInterceptor implements HandlerInterceptor {

    @Autowired
    private DataService dataService;
    @Autowired
    private UserService userService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        // 统计UV
        String ip = request.getRemoteHost();
        dataService.recordUV(ip);

        // 统计DAU
        String token = request.getHeader(JwtUtil.HEADER_TOKEN_KEY);
        if (token != null && !"null".equals(token)){
            int uid = Integer.parseInt(JwtUtil.getToken(token).getClaim("id").asString());
            User user = userService.findUserById(uid);
            dataService.recordDAU(user.getId());
        }

        return true;

    }
}
