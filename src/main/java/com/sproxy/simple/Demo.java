package com.sproxy.simple;

import com.sproxy.builder.ClassEnhance;
import com.sproxy.test.JavaBean;

public class Demo {


    public static void main(String[] args) {
        ClassEnhance enhance = new ClassEnhance();
        enhance.setProxyClass(JavaBean.class);
        enhance.setCallBack((obj,parameters,methodProxy)->{

            return methodProxy.invoke(obj,parameters);
        });


    }
}
