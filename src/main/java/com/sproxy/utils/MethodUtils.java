package com.sproxy.utils;

import com.sproxy.method.MethodFastClass;
import com.sproxy.method.MethodInfo;
import org.objectweb.asm.*;

import java.util.*;

public class MethodUtils implements Opcodes {


    public static byte[] createMethodFastClass(String proxyName,String fastName, String[] methodDescriptor) {
        Map<Integer, String> methodToIndex = new HashMap<>();
//        List<String> methodIndex = new ArrayList<>();
        int[] keys=null;
        /**
         * fastclass类名
         */
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        cw.visit(V1_8, ACC_PUBLIC | ACC_SUPER, fastName, null, "java/lang/Object", new String[]{Type.getInternalName(MethodFastClass.class)});
        MethodVisitor mv = MethodUtils.createInit(cw);
        {
            //getIndex
            mv = cw.visitMethod(ACC_PUBLIC, "getIndex", "(Ljava/lang/String;)I", null, null);
            mv.visitCode();
            mv.visitVarInsn(ALOAD, 1);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "hashCode", "()I", false);
            mv.visitVarInsn(ISTORE, 2);
            mv.visitVarInsn(ILOAD, 2);

            Label[] labels = new Label[methodDescriptor.length];
            keys = new int[methodDescriptor.length];
            for (int i = 0; i < labels.length; i++) {
                labels[i] = new Label();
            }
            Label defaultLabel = new Label();
            for (int i = 0; i < methodDescriptor.length; i++) {
                String s = methodDescriptor[i];
                keys[i] = s.replaceAll("\\|", "").hashCode();
                methodToIndex.put(keys[i], s);
            }
            Arrays.sort(keys);
            mv.visitLookupSwitchInsn(defaultLabel, keys, labels);
            for (int i = 0; i < labels.length; i++) {

                mv.visitLabel(labels[i]);
                if (i == 0) {
                    mv.visitFrame(F_APPEND, 1, new Object[]{INTEGER}, 0, null);
                    mv.visitInsn(ICONST_0);
                } else {
                    mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
                    if (i <= 5) {
                        mv.visitInsn(ICONST_0 + i);
                    } else {
                        mv.visitIntInsn(SIPUSH, i);
                    }
                }
                mv.visitInsn(IRETURN);
            }
            mv.visitLabel(defaultLabel);
            mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
            mv.visitInsn(ICONST_M1);
            mv.visitInsn(IRETURN);
            //随意
            mv.visitMaxs(1, 1);
            mv.visitEnd();
        }
        {
            mv = cw.visitMethod(ACC_PUBLIC, "invoke", "(ILjava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;", null, null);
            mv.visitCode();
            mv.visitVarInsn(ALOAD, 2);

            mv.visitTypeInsn(CHECKCAST, proxyName);
            mv.visitVarInsn(ASTORE, 4);
            Label[] labels = new Label[methodDescriptor.length];
            for (int i = 0; i < labels.length; i++) {
                labels[i] = new Label();
            }
            Label defaultLabel = new Label();
            mv.visitVarInsn(ILOAD, 1);
            mv.visitTableSwitchInsn(0, labels.length - 1, defaultLabel, labels);
            for (int i = 0; i < labels.length; i++) {
                mv.visitLabel(labels[i]);
                String[] methodInfo = methodToIndex.get(keys[i]).split("\\|");
                if (i == 0) {
                    mv.visitFrame(Opcodes.F_APPEND, 1, new Object[]{proxyName}, 0, null);
                } else {
                    mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
                }
                mv.visitVarInsn(ALOAD, 4);
                if (!methodInfo[1].startsWith("()")) {
                    //参数设置，强转参数
                    transfer(mv, methodInfo[1], 3);
                }
                mv.visitMethodInsn(INVOKEVIRTUAL, proxyName, methodInfo[0] + "$proxy", methodInfo[1], false);
                if (methodInfo[1].endsWith("V")) {
                    mv.visitInsn(ACONST_NULL);
                    mv.visitInsn(ARETURN);
                } else {
                    MethodUtils.chengReturnType(mv, methodInfo[1]);
                    mv.visitInsn(ARETURN);
                }

            }
            mv.visitLabel(defaultLabel);
            mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
            mv.visitInsn(ACONST_NULL);
            mv.visitInsn(ARETURN);
            mv.visitMaxs(1, 1);
            mv.visitEnd();
        }
        return cw.toByteArray();
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

//            System.out.print(typeClassName + "\t");
//            System.out.print(des + "\t");
//            System.out.print(descriptor + "\t");
//            System.out.println(argumentTypes.length);

            mv.visitVarInsn(ALOAD, index);
            mv.visitIntInsn(SIPUSH, i);
            mv.visitInsn(AALOAD);
            if (!descriptor.equals("[Ljava/lang/Object;")) {
                mv.visitTypeInsn(CHECKCAST, ClassUtils.getBox(typeClassName).replaceAll("\\.","/"));
            }
            if (!descriptor.contains("L")) {
                mv.visitMethodInsn(INVOKEVIRTUAL, box.replaceAll("\\.", "/"), typeClassName + "Value", "()" + descriptor, false);
            }
        }
        return mv;
    }

    /**
     * 创建默认构造方法
     * @param cw
     * @return
     */
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

    /**
     * 将返回值强转
     * @param mv
     * @param des
     */
    public static void chengReturnType(MethodVisitor mv, String des) {
        if (des.endsWith("V")) {
            return;
        }
        Type returnType = Type.getType(des).getReturnType();
        String descriptor = returnType.getDescriptor();
        if (descriptor.contains("L")) {
            mv.visitTypeInsn(CHECKCAST, "java/lang/Object");
            return;
        }
        mv.visitMethodInsn(INVOKESTATIC, ClassUtils.getBox(returnType.getClassName()).replaceAll("\\.", "/"), "valueOf", "(" + descriptor + ")L" + ClassUtils.getBox(returnType.getClassName()).replaceAll("\\.", "/") + ";", false);
    }
    public static int getVarInst(String s){
        switch (s){
            case "I":
            case "[I":
            case "S":
            case "B":
                return Opcodes.ILOAD;
            case "J":
                return Opcodes.LLOAD;
            case "C":
            case "D":
                return Opcodes.DLOAD;
            case "F":
            case "Z":
                return Opcodes.FLOAD;
            default:return Opcodes.ALOAD;
        }
    }

    public static int getReturnOpcode(Type type){
        String s = type.getReturnType().getDescriptor();
        switch (s){
            case "V":
                return RETURN;
            case "I":
            case "S":
            case "B":
            case "Z":
                return Opcodes.IRETURN;
            case "J":
                return Opcodes.LRETURN;
            case "C":
            case "D":
                return Opcodes.DRETURN;
            case "F":
                return Opcodes.FRETURN;
            default:return Opcodes.ARETURN;
        }
    }
}