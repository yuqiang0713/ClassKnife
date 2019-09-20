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
public @interface TryCatch {
    boolean extend() default false;
    String [] target();
}
