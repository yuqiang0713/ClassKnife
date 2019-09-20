package com.yuqiang.aop.util;


import com.yuqiang.aop.Configuration;

import org.objectweb.asm.Type;

import java.net.URLClassLoader;
import java.util.Objects;


/**
 * @author yuqiang
 */
public class ClassUtil {
    private static URLClassLoader sUrlClassLoader;

    public static void initClassLoader(URLClassLoader urlClassLoader) {
        sUrlClassLoader = urlClassLoader;
    }

    public static boolean isSuper(String className, String superName) {
        boolean flag;
        try {
            Class<?> superNameClass = sUrlClassLoader.loadClass(StringUtil.replaceSlash2Dot(superName));
            Class<?> classNameClass = sUrlClassLoader.loadClass(StringUtil.replaceSlash2Dot(className));
            flag = superNameClass.isAssignableFrom(classNameClass);
        } catch (Exception e) {
            flag = false;
        }
        return flag;
    }

    public static boolean isMethodExist(String propValue, String className, String methodName, String methodDesc) {
        boolean exist;
        try {
            if (RegexUtil.hasRegex(propValue)) {
                return true;
            }
            Class<?> clazz = sUrlClassLoader.loadClass(StringUtil.replaceSlash2Dot(className));
            Type[] paramTypes = Type.getArgumentTypes(methodDesc);
            int len = paramTypes.length;
            Class[] paramClasses = new Class[len];
            for (int i = 0; i < len; i++) {
                Type currentType = paramTypes[i];
                if (currentType.equals(Type.getType(boolean.class))) {
                    paramClasses[i] = boolean.class;
                } else if (currentType.equals(Type.getType(byte.class))) {
                    paramClasses[i] = byte.class;
                } else if (currentType.equals(Type.getType(char.class))) {
                    paramClasses[i] = char.class;
                } else if (currentType.equals(Type.getType(int.class))) {
                    paramClasses[i] = int.class;
                } else if (currentType.equals(Type.getType(float.class))) {
                    paramClasses[i] = float.class;
                } else if (currentType.equals(Type.getType(long.class))) {
                    paramClasses[i] = long.class;
                } else if (currentType.equals(Type.getType(double.class))) {
                    paramClasses[i] = double.class;
                } else if (currentType.equals(Type.getType(Object.class))) {
                    paramClasses[i] = Object.class;
                } else {
                    paramClasses[i] = sUrlClassLoader.loadClass(paramTypes[i].getClassName());
                }
            }
            if (CONSTRUCTOR_NAME.equals(methodName)) {
                clazz.getDeclaredConstructor(paramClasses);
                exist = true;
            } else if (STATIC_BLOCK_NAME.equals(methodName)) {
                exist = true;
            } else {
                clazz.getDeclaredMethod(methodName, paramClasses);
                exist = true;
            }
        } catch (ClassNotFoundException e) {
            exist = false;
        } catch (NoSuchMethodException e) {
            exist = false;
        }
        return exist;
    }

    public static boolean filter(String fileName, Configuration configuration) {
        if (configuration == null) {
            return filter(fileName);
        } else {
            return filter(fileName) && canScanClass(fileName, configuration);
        }
    }

    /**
     * 过滤class文件
     * @param fileName  source FilePath
     * @return true  can scan or knife
     *         false can not scan or knife
     */
    public static boolean filter(String fileName) {
        int index = fileName.lastIndexOf("/");
        String className = fileName.substring(index + 1);
        return className.endsWith(CLASS_SUFFIX) &&
                !Objects.equals(className, BUILD_CONFIG_CLASS) &&
                !Objects.equals(className, R_CLASS) &&
                !className.startsWith(R_INNER_CLASS);
    }

    private static boolean canScanClass(String fileName, Configuration configuration) {
        for (String item : configuration.exclude) {
            if ((StringUtil.replaceSlash2Dot(fileName).contains(item))) {
                return false;
            }
        }

        if (configuration.include.size() == 0) {
            return true;
        }

        for (String include : configuration.include) {
            if (StringUtil.replaceSlash2Dot(fileName).contains(include)) {
                return true;
            }
        }

        return false;
    }

    private static final String CLASS_SUFFIX = ".class";
    private static final String BUILD_CONFIG_CLASS = "BuildConfig.class";
    private static final String R_CLASS = "R.class";
    private static final String R_INNER_CLASS = "R$";
    private static final String CONSTRUCTOR_NAME = "<init>";
    private static final String STATIC_BLOCK_NAME = "<clinit>";
}
