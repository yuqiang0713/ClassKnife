package com.yuqiang.aop.annotations;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author yuqiang
 */
@Retention(RetentionPolicy.CLASS)
@Target({ElementType.TYPE})
public @interface Aspect {
}
