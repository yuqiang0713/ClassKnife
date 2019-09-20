package com.yuqiang.sampling;


import com.yuqiang.aop.annotations.Aspect;
import com.yuqiang.aop.annotations.Timing;

/**
 * Date : 2019/8/5
 * Time : 3:05 PM
 *
 * @author : yuqiang
 */
@Aspect
public class AopTimingConfig {

    @Timing(target = {"com.yuqiang.*.*(*)*"}, enter = true)
    public static void timingBefore(String desc) {
        SampingUtil.i(desc);
    }

    @Timing(target = {"com.yuqiang.*.*(*)*"}, enter = false)
    public static void timingAfter(String desc) {
        SampingUtil.o(desc);
    }
}
