package com.asmdemo.test1;

public class Bean {
    public Object javaBean;

    public void test(Object javaBean){
        this.javaBean=javaBean;
    }
    public void dd(){
        System.out.println(this.javaBean);
    }
}
