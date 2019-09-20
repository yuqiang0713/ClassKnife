package com.yuqiang.sampling;

import com.yuqiang.aop.annotations.Ignore;

/**
 * Date : 2019/8/5
 * Time : 3:06 PM
 *
 * @author : yuqiang
 */
@Ignore
public class TimingData {
    public String methodName;
    public long timeStamp;
    public boolean isMethodIn;

    public TimingData(String name, String in, long timeStamp) {
        this.methodName = name;
        this.isMethodIn = "in".equals(in);
        this.timeStamp = timeStamp;
    }

    @Override
    public String toString() {
        return "TimingData{" +
                "methodName='" + methodName + '\'' +
                ", timeStamp=" + timeStamp +
                ", isMethodIn=" + isMethodIn +
                '}';
    }
}
