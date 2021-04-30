package com.asmdemo.utils;

public class ClassUtils {
    public static String defineSubClassName(String className) {
        return className + "$0";
    }

    public static String defineSubClassPath(String className) {
        String replace = className.replaceAll("\\.", "/");
        if (replace.endsWith("$0.class")) {
            return replace;
        } else if (replace.endsWith(".class")) {
            return replace.replaceAll(".class", "$0.class");
        }
        return replace + "$0.class";
    }

    public static Class<?> getBasisClass(Class<?> c) {
        if (c == Integer.class) {
            return int.class;
        }else if (c==Short.class){
            return short.class;
        } else if (c == Long.class) {
            return long.class;
        } else if (c == Character.class) {
            return char.class;
        } else if (c == Double.class) {
            return double.class;
        } else if (c == Float.class) {
            return float.class;
        } else if (c==Boolean.class){
            return boolean.class;
        } else if (c==Byte.class){
            return byte.class;
        }
        return c;
    }


}
