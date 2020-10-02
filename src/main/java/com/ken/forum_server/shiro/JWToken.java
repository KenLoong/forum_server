package com.ken.forum_server.shiro;

import org.apache.shiro.authc.AuthenticationToken;

/**
 * 实现AuthenticationToken，结合shiro验证
 */
public class JWToken implements AuthenticationToken {

    private String token;

    public JWToken(String token) {
        this.token = token;
    }

    @Override
    public String getPrincipal() {
        return token;
    }

    @Override
    public String getCredentials() {
        return token;
    }
}
