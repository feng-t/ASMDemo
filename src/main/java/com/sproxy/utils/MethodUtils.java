package com.sproxy.utils;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

public class MethodUtils implements Opcodes {
    /**
     * 数组参数强转
     *
     * @param mv
     * @param des
     * @param index
     * @return
     */
    public static MethodVisitor transfer(MethodVisitor mv, String des, int index) {
        Type type = Type.getType(des);
        Type[] argumentTypes = type.getArgumentTypes();
        System.out.print("方法描述：");
        for (int i = 0; i < argumentTypes.length; i++) {
            Type argumentType = argumentTypes[i];
            String typeClassName = argumentType.getClassName();
            String box = ClassUtils.getBox(typeClassName);
            String descriptor = argumentType.getDescriptor();
            System.out.print(typeClassName+"\t");
            System.out.print(des+"\t");
            System.out.print(descriptor+"\t");
            System.out.println(argumentTypes.length);
            mv.visitVarInsn(ALOAD, index);

            if (argumentTypes.length>1) {
                mv.visitIntInsn(SIPUSH, i);
                mv.visitInsn(AALOAD);
            }
            if (!descriptor.equals("[Ljava/lang/Object;")) {
                mv.visitTypeInsn(CHECKCAST, descriptor);
            }
            if (!descriptor.contains("L")) {
                mv.visitMethodInsn(INVOKEVIRTUAL, box.replaceAll("\\.", "/"), typeClassName + "Value", "()" + descriptor, false);
            }
        }
        return mv;
    }

    public static MethodVisitor createInit(ClassWriter cw) {
        MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
        mv.visitCode();
        mv.visitVarInsn(ALOAD, 0);
        mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
        mv.visitInsn(RETURN);
        mv.visitMaxs(1, 1);
        mv.visitEnd();
        return mv;
    }

    public static void returnType(MethodVisitor mv, String des) {
        if (des.endsWith("V")){
            return;
        }
        Type returnType = Type.getType(des).getReturnType();
        String descriptor = returnType.getDescriptor();
        if (descriptor.contains("L")) {
            mv.visitTypeInsn(CHECKCAST, "java/lang/Object");
            return;
        }
        mv.visitMethodInsn(INVOKESTATIC, ClassUtils.getBox(returnType.getClassName()).replaceAll("\\.","/"), "valueOf", "("+descriptor+")L"+ ClassUtils.getBox(returnType.getClassName()).replaceAll("\\.","/")+";", false);
    }
}