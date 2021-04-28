package com.asmdemo.test;

public class ProxyBean extends JavaProxy{
    private String str;
    public ProxyBean(String sk,String k){
       super(sk);
    }
    public void test(){
        System.out.println(str);
    }
}
