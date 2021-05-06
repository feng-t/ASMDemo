package com.asmdemo;


import com.asmdemo.test.JavaProxy;

public class Demo extends ClassLoader{
    public static void main(String[] args) throws Exception {

        while (true){
            JavaProxy javaProxy = new JavaProxy();
            System.out.println(javaProxy.test1());
            System.out.println("---------------->");
            Thread.sleep(2000);
        }

    }



}
