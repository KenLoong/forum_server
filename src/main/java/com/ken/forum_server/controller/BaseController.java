package com.ken.forum_server.controller;

import com.auth0.jwt.exceptions.AlgorithmMismatchException;
import com.auth0.jwt.exceptions.InvalidClaimException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.ken.forum_server.exception.CustomException;
import com.ken.forum_server.exception.CustomExceptionCode;
import com.ken.forum_server.shiro.JWToken;
import com.ken.forum_server.util.JwtUtil;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;

@Controller
public class BaseController {

    @Autowired
    HttpServletRequest request;

    public int getUserId(HttpServletRequest request){
        String token = request.getHeader(JwtUtil.HEADER_TOKEN_KEY);
        return Integer.parseInt(JwtUtil.getToken(token).getClaim("id").asString());
    }

    public boolean isLogin(HttpServletRequest request){
        //获取请求头里的token
        String token = request.getHeader(JwtUtil.HEADER_TOKEN_KEY);

        if (StringUtils.isEmpty(token)){
            return false;
        }

        //验证token
        try {
            JwtUtil.verify(token);
        }catch (Exception e){
            return false;
        }
        return true;
    }
}
