package com.sproxy.simple;

import com.sproxy.method.MethodFastClass;
import com.sproxy.method.MethodInfo;
import org.objectweb.asm.Type;

public class Demo {
    private MethodInfo info;
    public void test(MethodFastClass fastClass){
        if (fastClass!=null){
            fastClass=null;
            test(fastClass);
        }
    }

    public static void main(String[] args) {
//        JavaBean$$proxy p = new JavaBean$$proxy((obj, parameter, proxy) -> {
//            System.out.println("执行前"+proxy.methodInfo());
//            Object invoke = proxy.invoke(obj, parameter);
//            System.out.println("执行后"+proxy.methodInfo());
//            return invoke;
//        });
//        int ggg = p.ggg(1, 9);
//        System.out.println(ggg);

        Type type = Type.getType("([IBZJLjava/lang/Integer;)V");
        Type[] argumentTypes = type.getArgumentTypes();
        for (Type argumentType : argumentTypes) {

            System.out.println(argumentType.getClassName()+":"+argumentType.getSort());
        }


    }
}
