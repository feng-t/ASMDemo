package com.sproxy.simple;

import com.sproxy.test.JavaBean$$proxy;

public class Demo {
    public static void main(String[] args) {
        JavaBean$$proxy p = new JavaBean$$proxy((obj, parameter, proxy) -> {
            System.out.println("执行前"+proxy.methodInfo());
            Object invoke = proxy.invoke(obj, parameter);
            System.out.println("执行后"+proxy.methodInfo());
            return invoke;
        });
        p.d("test");

    }
}
