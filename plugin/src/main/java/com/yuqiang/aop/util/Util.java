package com.yuqiang.aop.util;

import java.util.regex.Pattern;

public final class Util {

    private Util() {
    }

    public static boolean isNullOrNil(String str) {
        return str == null || str.isEmpty();
    }

    public static String nullAsNil(String str) {
        return str == null ? "" : str;
    }

    public static boolean isNumber(String str) {
        Pattern pattern = Pattern.compile("\\d+");
        return pattern.matcher(str).matches();
    }

    public static String byteArrayToHex(byte[] data) {
        char[] hexDigits = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
        char[] str = new char[data.length * 2];
        int k = 0;
        for (int i = 0; i < data.length; i++) {
            byte byte0 = data[i];
            str[k++] = hexDigits[byte0 >>> 4 & 0xf];
            str[k++] = hexDigits[byte0 & 0xf];
        }
        return new String(str);
    }

    public static String formatByteUnit(long bytes) {

        if (bytes >= 1024 * 1024) {
            return String.format("%.2fMB", bytes / (1.0 * 1024 * 1024));
        } else if (bytes >= 1024) {
            return String.format("%.2fKB", bytes / (1.0 * 1024));
        } else {
            return String.format("%dBytes", bytes);
        }
    }

    public static String globToRegexp(String glob) {
        StringBuilder sb = new StringBuilder(glob.length() * 2);
        int begin = 0;
        sb.append('^');
        for (int i = 0, n = glob.length(); i < n; i++) {
            char c = glob.charAt(i);
            if (c == '*') {
                begin = appendQuoted(sb, glob, begin, i) + 1;
                if (i < n - 1 && glob.charAt(i + 1) == '*') {
                    i++;
                    begin++;
                }
                sb.append(".*?");
            } else if (c == '?') {
                begin = appendQuoted(sb, glob, begin, i) + 1;
                sb.append(".?");
            }
        }
        appendQuoted(sb, glob, begin, glob.length());
        sb.append('$');
        return sb.toString();
    }

    private static int appendQuoted(StringBuilder sb, String s, int from, int to) {
        if (to > from) {
            boolean isSimple = true;
            for (int i = from; i < to; i++) {
                char c = s.charAt(i);
                if (!Character.isLetterOrDigit(c) && c != '/' && c != ' ') {
                    isSimple = false;
                    break;
                }
            }
            if (isSimple) {
                for (int i = from; i < to; i++) {
                    sb.append(s.charAt(i));
                }
                return to;
            }
            sb.append(Pattern.quote(s.substring(from, to)));
        }
        return to;
    }
}
