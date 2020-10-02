package com.ken.forum_server.annotation;

import java.lang.annotation.*;

/**
 * 带有此注解的方法无需token验证
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface TokenFree {
}
