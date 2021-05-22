package com.sproxy.test;

import com.sproxy.builder.ClassEnhance;
import com.sproxy.utils.ClassUtils;
import com.sproxy.utils.MethodUtils;
import org.objectweb.asm.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class CreateTest extends ClassLoader implements Opcodes {
    public byte[] test() {
        MethodVisitor mv;
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        cw.visit(V1_8, ACC_PUBLIC | ACC_SUPER, "com/sproxy/builder/ClassEnhance$JavaP929", null, "com/sproxy/test/JavaBean", null);

        cw.visitSource("ClassEnhance.java", null);
        cw.visitInnerClass("com/sproxy/builder/ClassEnhance$JavaP929", "com/sproxy/builder/ClassEnhance", "JavaP929", 0);
        {
            FieldVisitor fv = cw.visitField(ACC_FINAL + ACC_SYNTHETIC, "this$0", "Lcom/sproxy/builder/ClassEnhance;", null, null);
            fv.visitEnd();
        }
        {
            mv = cw.visitMethod(0, "<init>", "(Lcom/sproxy/builder/ClassEnhance;)V", null, null);
            mv.visitCode();
            Label l0 = new Label();
            mv.visitLabel(l0);
            mv.visitLineNumber(66, l0);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitFieldInsn(PUTFIELD, "com/sproxy/builder/ClassEnhance$JavaP929", "this$0", "Lcom/sproxy/builder/ClassEnhance;");
            mv.visitVarInsn(ALOAD, 0);
            mv.visitMethodInsn(INVOKESPECIAL, "com/sproxy/test/JavaBean", "<init>", "()V", false);
            mv.visitInsn(RETURN);
            Label l1 = new Label();
            mv.visitLabel(l1);
            mv.visitLocalVariable("this", "Lcom/sproxy/builder/ClassEnhance$JavaP929;", null, l0, l1, 0);
            mv.visitLocalVariable("this$0", "Lcom/sproxy/builder/ClassEnhance;", null, l0, l1, 1);
            mv.visitMaxs(2, 2);
            mv.visitEnd();
        }
        {
            mv = cw.visitMethod(ACC_PUBLIC, "test", "()V", null, null);
            mv.visitCode();
            Label l0 = new Label();
            mv.visitLabel(l0);
            mv.visitLineNumber(77, l0);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn(GETFIELD, "com/sproxy/builder/ClassEnhance$JavaP929", "this$0", "Lcom/sproxy/builder/ClassEnhance;");
            mv.visitFieldInsn(GETFIELD, "com/sproxy/builder/ClassEnhance", "methodCallBack", "Lcom/sproxy/method/MethodCallBack;");
            Label l1 = new Label();
            mv.visitJumpInsn(IFNULL, l1);
            Label l2 = new Label();
            mv.visitLabel(l2);
            mv.visitLineNumber(78, l2);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn(GETFIELD, "com/sproxy/builder/ClassEnhance$JavaP929", "this$0", "Lcom/sproxy/builder/ClassEnhance;");
            mv.visitFieldInsn(GETFIELD, "com/sproxy/builder/ClassEnhance", "methodCallBack", "Lcom/sproxy/method/MethodCallBack;");
            mv.visitVarInsn(ALOAD, 0);
            mv.visitInsn(ACONST_NULL);
            mv.visitTypeInsn(NEW, "com/sproxy/method/MethodProxy");
            mv.visitInsn(DUP);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn(GETFIELD, "com/sproxy/builder/ClassEnhance$JavaP929", "this$0", "Lcom/sproxy/builder/ClassEnhance;");
            mv.visitFieldInsn(GETFIELD, "com/sproxy/builder/ClassEnhance", "methodFastClass", "Lcom/sproxy/method/MethodFastClass;");
            mv.visitLdcInsn("test");
            mv.visitLdcInsn("()V");
            mv.visitMethodInsn(INVOKESPECIAL, "com/sproxy/method/MethodProxy", "<init>", "(Lcom/sproxy/method/MethodFastClass;Ljava/lang/String;Ljava/lang/String;)V", false);
            mv.visitMethodInsn(INVOKEINTERFACE, "com/sproxy/method/MethodCallBack", "invoke", "(Ljava/lang/Object;[Ljava/lang/Object;Lcom/sproxy/method/MethodProxy;)Ljava/lang/Object;", true);
            mv.visitInsn(POP);
            Label l3 = new Label();
            mv.visitJumpInsn(GOTO, l3);
            mv.visitLabel(l1);
            mv.visitLineNumber(80, l1);
            mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitMethodInsn(INVOKESPECIAL, "com/sproxy/test/JavaBean", "test", "()V", false);
            mv.visitLabel(l3);
            mv.visitLineNumber(82, l3);
            mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
            mv.visitInsn(RETURN);
            Label l4 = new Label();
            mv.visitLabel(l4);
            mv.visitLocalVariable("this", "Lcom/sproxy/builder/ClassEnhance$JavaP929;", null, l0, l4, 0);
            mv.visitMaxs(8, 1);
            mv.visitEnd();
        }
        ClassUtils.saveFile("/Users/hu/IdeaProjects/ASMDemo/target/classes/test.class", cw.toByteArray());

        return cw.toByteArray();
    }

    public Class<?> find(String name,byte[]bytes){
        return this.defineClass(name,bytes,0,bytes.length);
    }
    public static void main(String[] args) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        CreateTest c = new CreateTest();


        byte[] test = c.test();
        Class<?> aClass = c.defineClass("com.sproxy.builder.ClassEnhance$JavaP929", test, 0, test.length);


    }
}
