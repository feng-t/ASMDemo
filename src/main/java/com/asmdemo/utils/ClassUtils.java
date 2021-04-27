package com.asmdemo.utils;

public class ClassUtils {
    public static String defineSubClassName(String className){
        return className+"$0";
    }
    public static String defineSubClassPath(String className){
        String replace = className.replaceAll("\\.", "/");
        if (replace.endsWith("$0.class")){
            return replace;
        }else if (replace.endsWith(".class")){
            return replace.replaceAll(".class","$0.class");
        }
        return replace+"$0.class";
    }
}
