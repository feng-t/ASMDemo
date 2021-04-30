package com.asmdemo.test;

import com.asmdemo.utils.ClassUtils;
import com.asmdemo.utils.StringUtils;
import org.objectweb.asm.*;

import java.io.FileOutputStream;
import java.lang.reflect.Constructor;
import java.util.Arrays;

public class ProxyClass extends ClassLoader{
    public static void main(String[] args) throws Exception {

        ProxyClass aClass = new ProxyClass();
        ProxyBean bean = aClass.test1(ProxyBean.class);
        bean.test1();

    }

    public <T> T test1(Class<T> clazz,Object...parameters)throws Exception{


        ClassReader cr = new ClassReader(clazz.getName());
        ClassWriter cw = new ClassWriter(cr, ClassWriter.COMPUTE_MAXS);
        ClassAdapter ca = new ClassAdapter(cw){
            @Override
            public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
                StringUtils.println("{},{},{},{},{}",access,name,desc,signature,exceptions);

                if (name.equals("<init>")){
                    return super.visitMethod(access, name, desc, signature, exceptions);
                }
                if (name.equals("test1")){

                    MethodAdapter adapter = new MethodAdapter(cv.visitMethod(access, name, desc, signature, exceptions)){
                        @Override
                        public void visitMethodInsn(int opcode, String owner, String name1, String desc) {
                            StringUtils.println("visitMethodInsn:{},{},{},{}",opcode,owner,name1,desc);
                            //super.visitMethodInsn(opcode, owner, name1, desc);
                        }

                        @Override
                        public void visitFrame(int type, int nLocal, Object[] local, int nStack, Object[] stack) {
                            StringUtils.println("visitFrame:{},{},{},{},{}",type,nLocal,local,nStack,stack);

                        }
                    };

                    return adapter;
                }
                return null;
//                MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
//                if (name.equals("<init>")){
//                    mv=new MethodAdapter(mv){
//                        @Override
//                        public void visitMethodInsn(int opcode, String owner, String name, String desc) {
//                            StringUtils.println("{},{},{},{}",opcode,owner,name,desc);
//
//                            super.visitMethodInsn(opcode, owner, name, desc);
//                        }
//                    };
//                }
//                return mv;

            }

        };
        cr.accept(ca,ClassReader.SKIP_DEBUG);
        FileOutputStream stream = new FileOutputStream(Thread.currentThread().getContextClassLoader().getResource(".").getFile() + clazz.getName().replaceAll("\\.", "/") + ".class");
        stream.write(cw.toByteArray());
        stream.close();
        Class<T> aClass = (Class<T>) this.loadClass(clazz.getName());
        Class<?>[] classes={};
        if (parameters!=null){
//            classes = Arrays.stream(parameters).map(o->ClassUtils.getBasisClass(o.getClass())).toArray((p)->new Class<?>[parameters.length]);
            classes = Arrays.stream(parameters).map(Object::getClass).toArray((p)->new Class<?>[parameters.length]);
        }
        Constructor<T> constructor = aClass.getConstructor(classes);
        return constructor.newInstance(parameters);

    }
}
