package com.yuqiang.aop.util;

import java.util.Objects;

/**
 * Date : 2019/8/2
 * @time : 2:48 PM
 * @author yuqiang
 */
public class StringUtil {

    /**
     * 替换字符串斜线替换成点
     * @param name
     * @return
     */
    public static String replaceSlash2Dot(String name) {
        if (name == null || Objects.equals(name, "")) {
            return name;
        }
        return name.replace("/", ".");
    }
}
