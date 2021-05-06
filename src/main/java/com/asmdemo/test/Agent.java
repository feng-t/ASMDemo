package com.asmdemo.test;

import com.sun.tools.attach.*;

import java.io.IOException;
import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
import java.util.List;

public class Agent {
    public static void agentmain(String agentArgs, Instrumentation inst) {
        //指定自己定义的Transformer，在其中利用ASM做字节码替换 添加一个类文件转换器
        inst.addTransformer(new TestTransformer(), true);
        //重定义类并载入新的字节码
        try {
            inst.retransformClasses(JavaProxy.class);
        } catch (UnmodifiableClassException e) {
            e.printStackTrace();
        }
        System.out.println("Agent Load Done."+agentArgs);
    }

    public static void main(String[] args) throws AgentLoadException, IOException, AgentInitializationException, AttachNotSupportedException {
        List<VirtualMachineDescriptor> list = VirtualMachine.list();
        String s = "/Users/hu/IdeaProjects/ASMDemo/target/ASMDemo-1.0-SNAPSHOT.jar";
        for (VirtualMachineDescriptor vm : list) {
            System.out.println(vm.displayName());
            if (vm.displayName().equals("com.asmdemo.Demo")){
                VirtualMachine attach = VirtualMachine.attach(vm.id());
                attach.loadAgent(s,"test");
                attach.detach();
            }
        }
    }
}
