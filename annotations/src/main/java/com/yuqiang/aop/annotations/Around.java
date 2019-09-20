package com.yuqiang.aop.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author yuqiang
 */
@Retention(RetentionPolicy.CLASS)
@Target({ElementType.METHOD})
public @interface Around {
    boolean enter() default true;//默认相当于before false相当于after
    boolean extend() default false;
    String[] target();
}
