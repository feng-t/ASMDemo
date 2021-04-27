package com.asmdemo.core;

import com.asmdemo.test.JavaProxy;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public class Test {
    public static void main(String[] args) throws IOException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
//        create();
//        JavaProxy$1 $1 = new JavaProxy$1();
//        $1.test1();

    }
    public static void create() throws IOException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        ASMCreate create = new ASMCreate();
        create.setSave(true);
        JavaProxy o = (JavaProxy) create.setSuperClass(JavaProxy.class).create();
//        Class<?> aClass = create.loadClass("com.asmdemo.test.JavaProxy$0");
//        JavaProxy o = (JavaProxy) aClass.newInstance();
        o.test1();
        System.out.println(o.a);
    }
}
