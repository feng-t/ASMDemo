package com.sproxy.test;

public class test2 {

    static {
        System.out.println("t2");
    }
    public void df(Object obj) {
        test1 ts = (test1) obj;
        System.out.println("df");
    }
}
