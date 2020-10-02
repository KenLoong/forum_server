package com.ken.forum_server.common;

import com.ken.forum_server.exception.CustomException;
import com.ken.forum_server.exception.CustomExceptionCode;
import org.apache.shiro.UnavailableSecurityManagerException;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authz.AuthorizationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 通用全局异常处理
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理自定义异常
     * @param e
     * @return
     */
    @ExceptionHandler(CustomException.class)
    public Result handleCustomException(CustomException e){
        return new Result(e);
    }


    /**
     * 处理自定义异常
     * @param e
     * @return
     */
    @ExceptionHandler(AuthenticationException.class)
    public Result handleShiroException(AuthenticationException e){
        //在userRealm抛出的异常，会被shiro框架转成AuthenticationException，它的cause才是我们原本抛出的异常
        if (e.getCause() instanceof  CustomException){
            return new Result((CustomException)e.getCause());
        }
        return new Result(CustomExceptionCode.SYSTEM_ERROR);
    }


    /**
     * 处理权限不足异常
     * @param e
     * @return
     */
    @ExceptionHandler(AuthorizationException.class)
    public Result handleAuthorizationException(AuthorizationException e){
        return new Result(CustomExceptionCode.PERMISSION_ERROR);
    }

    //在拦截器那里，如果拦截了一个不存在的路径，然后去调用shiro的login方法就会出现这个异常
    //因为你所请求的URL是不在Shiro所管辖范围的，而你又在你请求的这个URL后试图通过Shiro来获取Session，
    // 所以对Shiro来说就是 “你不让我负责的事，为什么要跟我要结果”
    @ExceptionHandler(UnavailableSecurityManagerException.class)
    public Result handleUnavailableSecurityManagerException(UnavailableSecurityManagerException e){
        System.out.println("-----不存在的路径---------");
        System.out.println(e.getMessage());
        return new Result(CustomExceptionCode.PATH_ERROR);
    }

    /**
     * 处理未知异常
     * @return
     */
    @ExceptionHandler(Exception.class)
    public Result handleSystemException(Exception e){
        System.out.println("-----未知异常--------------");
        e.printStackTrace();
        return new Result(CustomExceptionCode.SYSTEM_ERROR);
    }

}
