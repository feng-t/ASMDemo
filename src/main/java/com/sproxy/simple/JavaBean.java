package com.sproxy.simple;

public class JavaBean {

    public JavaBean() {
    }


    public void d(String s) {
        System.out.println("方法 d 被调用：" + s);
    }

    public void test() {
        System.out.println("方法 test 被调用");
    }

    public void test2(int a,Integer b,boolean c){

    }


    public int t1(int i1, boolean i2, int i3) {
        return i1;
    }
    public int t2() {
        return 8;
    }
    public int[] t2(int[] a){
        return a;
    }
    public int[] t3(int a){
        return new int[]{a};
    }
    public int t3(int[] a){
        return 9;
    }
}
