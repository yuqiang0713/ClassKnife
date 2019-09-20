package com.yuqiang.sampling;

import com.yuqiang.aop.annotations.Ignore;

import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Date : 2019/8/5
 * Time : 3:05 PM
 *
 * @author : yuqiang
 */
@Ignore
public class SampingUtil {

    private static List<TimingData> timingDataList = new CopyOnWriteArrayList<>();
    private static Map<String, Integer> MAP = new ConcurrentHashMap<>();
    private static AtomicInteger COUNT = new AtomicInteger(0);

    public static synchronized void i(String name) {
        int lastIndex = name.lastIndexOf("(");
        String className = name.substring(0, lastIndex);
        int index = COUNT.getAndIncrement();
        timingDataList.add(index, new TimingData(className, "in", System.currentTimeMillis()));
        MAP.put(className + "in", index);
    }

    public static synchronized void o(String name) {
        int lastIndex = name.lastIndexOf("(");
        String className = name.substring(0, lastIndex);
        int index = COUNT.getAndIncrement();
        timingDataList.add(index, new TimingData(className, "out", System.currentTimeMillis()));
        MAP.put(className + "out", index);
    }

    public static List<TimingData> queryByName(String name) {
        if (MAP.containsKey(name + ".in") && MAP.containsKey(name + ".out")) {
            Integer start = SampingUtil.MAP.get(name + ".in");
            Integer end = SampingUtil.MAP.get(name + ".out");
            if (start == null || end == null) {
                return null;
            }

            if (start > end || start == -1 || end == -1) {
                return null;
            }

            return timingDataList.subList(start, end + 1);
        } else {
            return null;
        }

    }

    public static void getStack(String desc, PrintWriter writer) {
        List<TimingData> timingDataList = queryByName(desc);
        Node.analysis(timingDataList, writer);
    }
}
