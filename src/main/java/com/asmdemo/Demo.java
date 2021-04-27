package com.asmdemo;


import com.asmdemo.test.JavaProxy;

public class Demo {
    public static void main(String[] args) throws Exception {

        while (true){
            JavaProxy proxy = new JavaProxy();
            proxy.test1();

            Thread.sleep(5000);
        }

    }



}
