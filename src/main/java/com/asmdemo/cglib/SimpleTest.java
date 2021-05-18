package com.asmdemo.cglib;

import com.asmdemo.cglib.proxy.MethodCallBack;
import com.asmdemo.utils.FileUtils;
import org.objectweb.asm.*;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

public class SimpleTest extends ClassLoader implements Opcodes {

    private Class<?> superClass;
    private MethodCallBack methodCallBack;

    public void setSuperClass(Class<?> superClass) {
        this.superClass = superClass;
    }

    public void setMethodCallBack(MethodCallBack methodCallBack) {
        this.methodCallBack = methodCallBack;
    }


    Object create() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        return create(new Object[0],new Class[0]);
    }
    Object create(Object[] args,Class<?>[] classes) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        ClassReader cr;
        ClassWriter cw = null;
        ClassBuilder adapter = null;
        try {
            cr = new ClassReader(superClass.getName());
            cw = new ClassWriter(cr, ClassWriter.COMPUTE_MAXS);
            adapter = new ClassBuilder(cw);
            cr.accept(adapter, ClassReader.SKIP_DEBUG);
        } catch (IOException e) {
            e.printStackTrace();
        }
        byte[] bytes = cw.toByteArray();
        String path = Thread.currentThread().getContextClassLoader().getResource(".").getPath()
                + superClass.getName().replaceAll("\\.", "/") + adapter.getSuffix() + ".class";
        String s = FileUtils.saveFile(path, bytes);
        System.out.println(s);
        Class<?> defineClass = this.defineClass(adapter.getClassName(), bytes, 0, bytes.length);
        Constructor<?> constructor = defineClass.getConstructor(classes);
        return constructor.newInstance(args);
    }
    static class ClassBuilder extends ClassVisitor{
        private int access;
        private String suffix;
        private String className;

        public String getSuffix() {
            return suffix;
        }

        public String getClassName() {
            return className.replaceAll("/",".");
        }

        /**
         * Constructs a new {@link ClassVisitor} object.
         *
         * @param cv the class visitor to which this adapter must delegate calls.
         */
        public ClassBuilder(ClassVisitor cv) {
            super(Opcodes.ASM9,cv);
        }

        @Override
        public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
            this.access=access;
            this.suffix=(access&ACC_INTERFACE)==0?"$Sub":"$Impl";
            this.className=name+suffix;
            if ((access&ACC_INTERFACE)!=0){
                String[] newInterfaces = new String[interfaces.length + 1];
                for (int i = 0; i < interfaces.length; i++) {
                    newInterfaces[i]=interfaces[i];
                }
                newInterfaces[interfaces.length]=name;
                super.visit(version, (~ACC_ABSTRACT)&(~ACC_INTERFACE) & access, name+suffix, signature, Type.getInternalName(Object.class), newInterfaces);
                {
                    MethodVisitor mv = cv.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
                    mv.visitCode();
                    mv.visitVarInsn(ALOAD, 0);
                    mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V");
                    mv.visitInsn(RETURN);
                    mv.visitMaxs(1, 1);
                    mv.visitEnd();
                }
            }else {
                super.visit(version, access, name, signature, superName, interfaces);
            }
        }

        @Override
        public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
            if ((this.access&ACC_INTERFACE)!=0) {
                return super.visitField((~ACC_ABSTRACT)&(~ACC_INTERFACE) & access, name, desc, signature, value);
            }
            return null;
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
            if ((this.access&ACC_INTERFACE)!=0) {
                return super.visitMethod((~ACC_ABSTRACT)&(~ACC_INTERFACE) & access, name, desc, signature, exceptions);
            }
            return null;
        }
    }

    public static void main(String[] args) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        SimpleTest test = new SimpleTest();
        test.setSuperClass(MethodCallBack.class);
        test.create();
//        String name = Type.getInternalName(Object.class);
//        System.out.println(name);
    }
}
