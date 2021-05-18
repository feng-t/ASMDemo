package com.asmdemo.cglib.proxy;

public class JavaBean {
    public void test1(){
        System.out.println("test1");
    }
    public int test2(){
        System.out.println("test2");
        return 9;
    }
    public int test3(int a,int b){
        System.out.println("test3 add");
        return a+b;
    }

}
