package com.asmdemo.agent;


public class JavaProxy {

    public JavaProxy() {
    }

    public int test1() {
        System.out.println("执行test");
        return 99;
    }


    public Object test1(Object[] str) {

        return this.test2((String[]) str);
    }

    public int test2(String[] str) {
        return 89;
    }

}
