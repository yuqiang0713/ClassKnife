package com.yuqiang.aop.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author yuqiang
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.METHOD)
public @interface Interceptor {
    boolean extend() default false;
    boolean returnValue() default false; // 返回true表示拦截 返回false表示拦截 默认false
    String[] target();
}
