package com.ken.forum_server.exception;

public enum  CustomExceptionCode implements ICustomExceptionCode {

    SYSTEM_ERROR(100,"系统故障，请稍后再试..."),
    TOKEN_ERROR(201,"你的账户信息有问题，请重新登录"),
    TOKNE_EXPIRED(202,"你的令牌已过期，请重新登录"),
    PATH_ERROR(301,"你放问的页面不存在"),
    COMMENT_ERROR(302,"评论内容不可为空"),
    USER_NOT_EXIST(303,"用户不存在"),
    SEND_REPEAT(304,"不可以发给自己私信！"),
    PERMISSION_ERROR(305,"没有权限进行此操作！")
    ;

    private final int code;
    private final String message;

    CustomExceptionCode(int code , String message){
        this.code = code;
        this.message = message;
    }

    @Override
    public Integer getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
