package com.asmdemo.test;

import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;

public class Agent {
    public static void agentmain(String agentArgs, Instrumentation inst) {
        //指定我们自己定义的Transformer，在其中利用Javassist做字节码替换 添加一个类文件转换器

        inst.addTransformer(new TestTransformer(), true);
        //重定义类并载入新的字节码

        try {
            inst.retransformClasses(JavaProxy.class);
        } catch (UnmodifiableClassException e) {
            e.printStackTrace();
            System.out.println("报错了---");
        }catch (Exception e){
            System.out.println("不知道什么原因");
        }
        System.out.println("Agent Load Done."+agentArgs);

    }

}
