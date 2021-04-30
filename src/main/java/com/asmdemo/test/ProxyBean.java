package com.asmdemo.test;

public class ProxyBean extends JavaProxy{
    private String str;
    public ProxyBean(){
       super("dfghj");
    }
    public void test1(){
        super.test1();
    }
}
