package com.sproxy.test;

import com.sproxy.method.MethodFastClass;
import com.sproxy.method.MethodFastClassBuilder;

import java.util.Arrays;

public class test1 {
    public static test2 t2;

    public test1(test2 t2){
        test1.t2 =t2;
    }
    static {
        System.out.println("t1 static");
    }

    public test1() {

    }


    public void test222(Object... as){
        testddd((Object[][]) as[0]);
    }
    public void testddd(Object[][] ass){
        System.out.println(Arrays.deepToString(ass));
    }
    public static void main(String[] args) throws InstantiationException, IllegalAccessException {
//        test1 t1 = new test1();
//        t1.test222((Object) new Object[][]{});

        MethodFastClass fastClass = MethodFastClassBuilder.getInstance().create(test1.class);
        System.out.println("创建成功");
    }
}
