package com.sproxy.method;

import com.sproxy.utils.ClassUtils;
import com.sproxy.utils.MethodUtils;
import org.objectweb.asm.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 创建一个fastclass
 */
public class MethodFastClassBuilder extends ClassLoader implements Opcodes {
    public static Map<String, byte[]> map = new ConcurrentHashMap<>();
    public static Map<String, MethodFastClass> instances = new ConcurrentHashMap<>();

    private MethodFastClassBuilder() {
    }

    private static class INSTANCE {
        public static MethodFastClassBuilder builder = new MethodFastClassBuilder();
    }

    public static MethodFastClassBuilder getInstance() {
        return INSTANCE.builder;
    }

    /**
     * 方法描述
     */
    public List<String> methodDescriptors = Collections.synchronizedList(new ArrayList<>());


    public Class<MethodFastClass> build(Class<?> proxyClass) {
        String proxyName = proxyClass.getName().replaceAll("\\.", "/") + "$$proxy";
        String fastClass = proxyClass.getName() + "$$FastClass";
        byte[] bytes = map.get(proxyName);
        if (bytes == null) {
            List<String> method = null;
            try {
                method = processMethod(proxyClass);
                bytes = MethodUtils.createMethodFastClass(proxyName, fastClass.replaceAll("\\.", "/"), method.toArray(new String[0]));
                ClassUtils.saveFile("/Users/hu/IdeaProjects/ASMDemo/target/classes/fastClass.class",bytes);
            } catch (IOException e) {
                e.printStackTrace();
            }
            map.put(proxyName, bytes);
        }
        return (Class<MethodFastClass>) defineClass(fastClass, bytes, 0, bytes.length);
    }

    /**
     * 只允许代理类调用，因为此方法再要代理类被加载之后调用
     * @param proxyClass
     * @return
     */
    public MethodFastClass create(Class<?>proxyClass) {
        String name = proxyClass.getName();
        MethodFastClass methodFastClass = instances.get(name);
        if (methodFastClass==null){
            Class<MethodFastClass> build = build(proxyClass);
            try {
                methodFastClass = build.newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
            instances.put(name,methodFastClass);
        }
        return methodFastClass;
    }

    /**
     * @throws IOException
     */
    private static List<String> processMethod(Class<?> proxyClass) throws IOException {
        List<String> list = new ArrayList<>();
        ClassReader cr = new ClassReader(proxyClass.getName());
        ClassWriter cw = new ClassWriter(cr, 0);
        ClassVisitor cv = new ClassVisitor(ASM9, cw) {
            @Override
            public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
                if (!name.equals("<init>")&&!name.equals("<clinit>")) {
                    list.add(name + "|" + descriptor);
                }
                return super.visitMethod(access, name, descriptor, signature, exceptions);
            }
        };
        cr.accept(cv, 0);
        return list;
    }

//
//    public static void main(String[] args) throws IOException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
//        Class<JavaBean> beanClass = JavaBean.class;
//        String proxyClass = beanClass.getName().replaceAll("\\.", "/") + "$proxy" + (UUID.randomUUID().toString().substring(0, 5));
//        MethodFastClassBuilder builder = new MethodFastClassBuilder(beanClass, proxyClass);
//        byte[] bytes = MethodUtils.createMethodFastClass(proxyClass,builder.methodDescriptors.toArray(new String[0]));
//        Class<?> aClass = builder.defineClass((proxyClass+"$$MethodFastClass").replaceAll("/", "\\."), bytes, 0, bytes.length);
//        Method index = aClass.getMethod("getIndex", String.class);
//
//        Object invoke = index.invoke(aClass.newInstance(), "test1()V");
//        System.out.println(invoke);
//
//    }
}
