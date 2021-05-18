package com.asmdemo.cglib.create;


import com.asmdemo.cglib.proxy.MethodCallBack;
import com.asmdemo.cglib.proxy.MethodProxy;
import com.asmdemo.utils.FileUtils;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CreateBuilder extends ClassLoader implements Opcodes {
    public Map<MethodDescriptor, MethodCreateBack> methodDescriptorMap = new ConcurrentHashMap<>();
    public ClassWriter cw;
    public int access;
    public String superName;
    public String signature;
    public String className;
    public String[] interfaces;

    public CreateBuilder(int access, String className, String signature, String superName, String[] interfaces) {
        this.cw = new ClassWriter(ASM9);
        this.access = access;
        this.superName = superName;
        this.signature = signature;
        this.className = className;
        this.interfaces = interfaces;
    }

    public byte[] start() {
        cw.visit(V1_8, access, className, signature, superName, interfaces);
        addMethodDescriptorMap(new MethodDescriptor(ACC_PUBLIC, "<init>", "()V", null, null), (mv) -> {
            mv.visitCode();
            mv.visitVarInsn(ALOAD, 0);
            mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
            mv.visitInsn(RETURN);
            mv.visitMaxs(1, 1);
            mv.visitEnd();
        });
        return cw.toByteArray();
    }

    public void methodBuild() {
        for (Map.Entry<MethodDescriptor, MethodCreateBack> entry : methodDescriptorMap.entrySet()) {
            MethodDescriptor key = entry.getKey();
            MethodCreateBack value = entry.getValue();
            value.create(cw.visitMethod(key.access, key.name, key.descriptor, key.signature, key.exceptions));
        }
    }

    public void addMethodDescriptorMap(MethodDescriptor methodDescriptor, MethodCreateBack back) {
        this.methodDescriptorMap.put(methodDescriptor, back);
    }

    public static void main(String[] args) {
        CreateBuilder builder = new CreateBuilder(ACC_PUBLIC, "com.asmdemo.cglib.create.me", null, Type.getInternalName(Object.class), new String[]{Type.getInternalName(MethodCallBack.class)});

        byte[] start = builder.start();
        FileUtils.saveFile("/Users/hu/IdeaProjects/ASMDemo/target/classes/com/me.class", start);
        Class<?> defineClass = builder.defineClass("com.asmdemo.cglib.create.me", start, 0, start.length);

        System.out.println();
    }


    interface MethodCreateBack {
        void create(MethodVisitor mv);
    }

    static class MethodDescriptor {
        public int access;
        public String name;
        public String descriptor;
        public String signature;
        public String[] exceptions;

        public MethodDescriptor(int access, String name, String descriptor, String signature, String[] exceptions) {
            this.access = access;
            this.name = name;
            this.descriptor = descriptor;
            this.signature = signature;
            this.exceptions = exceptions;
        }
    }
}
