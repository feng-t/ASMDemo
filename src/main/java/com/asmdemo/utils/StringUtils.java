package com.asmdemo.utils;

import java.util.Arrays;

public class StringUtils {
    public static void println(Object...obj){
        System.out.println(Arrays.toString(obj));
    }
    public static void println(String str,Object...obj){
        for (Object o : obj) {
            str=str.replaceFirst("\\{\\}",o==null?"null":o.toString());
        }
        System.out.println(str);
    }
}
