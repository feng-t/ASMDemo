package com.asmdemo.utils;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

public class MethodUtils implements Opcodes {

    public static void main(String[] args) {
        returnType(null, "(Ljava/lang/Double;Ljava/lang/Integer;II)I");
//        Type types = Type.getType("(I)V");
//        System.out.println(Arrays.toString(types.getArgumentTypes()));
    }

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
        for (int i = 0; i < argumentTypes.length; i++) {
            Type argumentType = argumentTypes[i];
            String typeClassName = argumentType.getClassName();
            String box = ClassUtils.getBox(typeClassName);
            String descriptor = argumentType.getDescriptor();
            mv.visitVarInsn(ALOAD, index);
            mv.visitIntInsn(SIPUSH, i);
            mv.visitInsn(AALOAD);
            mv.visitTypeInsn(CHECKCAST, box.replaceAll("\\.", "/"));
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
//        mv.visitTypeInsn(CHECKCAST, ClassUtils.getBox(returnType.getClassName()).replaceAll("\\.","/"));
        mv.visitMethodInsn(INVOKESTATIC, ClassUtils.getBox(returnType.getClassName()).replaceAll("\\.","/"), "valueOf", "("+descriptor+")L"+ ClassUtils.getBox(returnType.getClassName()).replaceAll("\\.","/")+";", false);
    }
}
