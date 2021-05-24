package com.sproxy.simple;

import com.sproxy.builder.ClassEnhance;

public class Demo {


    public static void main(String[] args) {
        String s = Demo.class.getClassLoader().getResource(".").getPath();
        System.setProperty(ClassEnhance.fastClassPath, s + "fastClass");
        System.setProperty(ClassEnhance.proxyClassPath, s + "proxyClass");

        ClassEnhance enhance = new ClassEnhance();
        enhance.setProxyClass(JavaBean.class);
        enhance.setCallBack((obj, parameters, methodProxy) -> {
            System.out.println("执行前");
            Object invoke = methodProxy.invoke(obj, parameters);
            System.out.println("执行后--->结果：" + invoke);
            return invoke;
        });

        JavaBean o = (JavaBean) enhance.create();
        int i = o.t1(1, false, 3);

    }
}
