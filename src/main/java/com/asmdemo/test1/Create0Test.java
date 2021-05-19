package com.asmdemo.test1;

import com.asmdemo.cglib.proxy.JavaBean;
import com.asmdemo.cglib.proxy.MethodCallBack;
import com.asmdemo.cglib.proxy.MethodProxy;
import com.asmdemo.utils.ClassUtils;
import org.objectweb.asm.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class Create0Test extends ClassLoader implements Opcodes  {
    public static void main(String[] args) throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        new Create0Test().test();
//        System.out.println(Type.getDescriptor(ClassWriter.class));
    }
    public void test() throws InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);

        cw.visit(V1_8,ACC_PUBLIC+ACC_SUPER,"com/asmdemo/proxy/create",null, Type.getInternalName(Object.class),new String[]{MethodCallBack.class.getName().replaceAll("\\.","/")});

        MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
        mv.visitCode();
        mv.visitVarInsn(ALOAD, 0);
        mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", true);
        mv.visitInsn(RETURN);
        mv.visitMaxs(1, 1);
        mv.visitEnd();
        mv= cw.visitMethod(ACC_PUBLIC, "invoke", "(" + Type.getDescriptor(Object.class) + Type.getDescriptor(Object[].class)+  Type.getDescriptor(MethodProxy.class)+ ")" + Type.getDescriptor(Object.class), null, null);
        mv.visitCode();
        mv.visitInsn(ACONST_NULL);
        mv.visitInsn(ARETURN);
        mv.visitMaxs(1,1);
        mv.visitEnd();

        cw.visitEnd();
        String name = JavaBean.class.getPackage().getName().replaceAll("\\.","\\\\")+"\\\\create";
//        ClassUtils.saveClass("F:\\projects\\ASMDemo\\target\\classes\\"+name,cw.toByteArray());

        byte[] bytes = cw.toByteArray();
        Class<?> defineClass = this.defineClass("com.asmdemo.proxy.create", bytes, 0, bytes.length);
        Object o = defineClass.newInstance();
//
        cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        cw.visit(V1_8, ACC_PUBLIC, "com/asmdemo/proxy/create2", null, Type.getInternalName(Object.class), null);

        FieldVisitor test = cw.visitField(ACC_PUBLIC, "test", "Lcom/asmdemo/proxy/create;", null, null);
        test.visitEnd();

        mv = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
        mv.visitCode();
        mv.visitVarInsn(ALOAD, 0);
        mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", true);
        mv.visitInsn(RETURN);
        mv.visitMaxs(1, 1);
        mv.visitEnd();
        mv = cw.visitMethod(ACC_PUBLIC, "invoke", "(Lcom/asmdemo/proxy/create;)V", null, null);
        mv.visitCode();
        mv.visitVarInsn(ALOAD,0);
        mv.visitVarInsn(ALOAD,1);
        mv.visitFieldInsn(PUTFIELD,"com/asmdemo/proxy/create2","test","Lcom/asmdemo/proxy/create;");

        mv.visitInsn(RETURN);
        mv.visitMaxs(2, 1);
        mv.visitEnd();

        {
            {
                mv = cw.visitMethod(ACC_PUBLIC, "dd", "()V", null, null);
                mv.visitCode();
                mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
                mv.visitVarInsn(ALOAD, 0);
                mv.visitFieldInsn(GETFIELD, "com/asmdemo/proxy/create2","test","Lcom/asmdemo/proxy/create;");
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/Object;)V", false);
                mv.visitInsn(RETURN);
                mv.visitMaxs(2, 1);
                mv.visitEnd();
            }
        }
        cw.visitEnd();

        ClassUtils.saveClass("F:\\projects\\ASMDemo\\target\\classes\\"+name+"$proxy2",cw.toByteArray());
        bytes = cw.toByteArray();
        Class<?> create2 = this.defineClass("com.asmdemo.proxy.create2", bytes, 0, bytes.length);
        Object o2 = create2.newInstance();
        Method test1 = create2.getMethod("invoke", o.getClass());
        test1.invoke(o2,o);
        Method dd = create2.getMethod("dd");
        dd.invoke(o2);
    }


}
