package com.ken.forum_server.util;

import org.apache.shiro.crypto.hash.SimpleHash;
import org.springframework.beans.factory.annotation.Value;

public class MD5Util {

    //加密盐值
    private static String SALT = "daskodakfap*!";

    /**
     * 密码加密
     * @return
     */
    public static String md5Encryption(String originPassword){
        System.out.println("加密的盐值是！！"+SALT);
        String algorithmName = "MD5";//加密算法
        int hashIterations = 5;//加密次数
        SimpleHash simpleHash = new SimpleHash(algorithmName,originPassword,SALT,hashIterations);
        return simpleHash+"";
    }
}