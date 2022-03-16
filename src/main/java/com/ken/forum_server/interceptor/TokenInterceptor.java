package com.ken.forum_server.interceptor;

import com.ken.forum_server.annotation.TokenFree;
import com.ken.forum_server.exception.CustomException;
import com.ken.forum_server.shiro.JWToken;
import com.ken.forum_server.util.JwtUtil;
import org.apache.shiro.SecurityUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 拦截器，检查被访问资源是否需要验证token
 */
public class TokenInterceptor implements HandlerInterceptor {

    /**
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws CustomException
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws CustomException {


        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            System.out.println("拦截了 : " + ((HandlerMethod) handler).getMethod().getName());
            TokenFree tokenFree = handlerMethod.getMethodAnnotation(TokenFree.class);
            //无需token验证，直接放行
            if (tokenFree != null) {
                return true;
            }

            //获取请求头里的token
            String token = request.getHeader(JwtUtil.HEADER_TOKEN_KEY);

            if (StringUtils.isEmpty(token)) {
                throw new CustomException("请先登录");
            }
            //交给shiro验证token是否正确
            try {
                SecurityUtils.getSubject().login(new JWToken(token));
            } catch (CustomException e) {
                throw e;
            }

            return true;
        }

        return true;
    }
}
