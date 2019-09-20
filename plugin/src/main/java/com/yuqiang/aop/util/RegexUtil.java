package com.yuqiang.aop.util;

/**
 * @author yuqiang
 */
public final class RegexUtil {

    public static final String REG_STAR = "*";
    private static final String REG_FULL_MATCH = "[\\s\\S]*";
    private static final String REPLACE_LEFT_BRACKET = "\\(";
    private static final String REPLACE_RIGHT_BRACKET = "\\)";
    private static final String LEFT_BRACKET = "(";
    private static final String RIGHT_BRACKET = ")";

    public static String replace(String regStr) {
        if (regStr.contains(LEFT_BRACKET) && regStr.contains(RIGHT_BRACKET) && regStr.contains(REG_STAR)) {
            return regStr.replace(REG_STAR, REG_FULL_MATCH).replace(LEFT_BRACKET, REPLACE_LEFT_BRACKET).replace(RIGHT_BRACKET, REPLACE_RIGHT_BRACKET);
        } else if (regStr.contains(REG_STAR)) {
            return regStr.replace(REG_STAR, REG_FULL_MATCH);
        } else {
            return regStr;
        }
    }

    static boolean hasRegex(String regStr) {
        return regStr.contains(REG_STAR);
    }
}
