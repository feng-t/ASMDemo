package com.asmdemo.test;

import com.asmdemo.core.ClassHandler;
import com.asmdemo.utils.StringUtils;
import org.objectweb.asm.*;

import java.io.FileOutputStream;
import java.lang.reflect.Constructor;

public class ProxyClass extends ClassLoader{
    public static void main(String[] args) throws Exception {

        ProxyClass aClass = new ProxyClass();
        aClass.test1(ProxyBean.class.getName());
    }
    public void test1(String name)throws Exception{


        ClassReader cr = new ClassReader(name);
        ClassWriter cw = new ClassWriter(cr, ClassWriter.COMPUTE_MAXS);
        ClassAdapter ca = new ClassAdapter(cw){
            @Override
            public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
                StringUtils.println("{},{},{},{},{}",access,name,desc,signature,exceptions);

                MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
                if (name.equals("<init>")){
                    mv=new MethodAdapter(mv){
                        @Override
                        public void visitMethodInsn(int opcode, String owner, String name, String desc) {
                            StringUtils.println("{},{},{},{}",opcode,owner,name,desc);

                            super.visitMethodInsn(opcode, owner, name, desc);
                        }
                    };
                }
                return mv;

            }
        };
        cr.accept(ca,ClassReader.SKIP_DEBUG);
        FileOutputStream stream = new FileOutputStream(Thread.currentThread().getContextClassLoader().getResource(".").getFile() + name.replaceAll("\\.", "/") + ".class");
        stream.write(cw.toByteArray());
        stream.close();
        Class<?> aClass = this.loadClass(name);
        Constructor<?> constructor = aClass.getConstructor(String.class);
        ProxyBean tes = (ProxyBean) constructor.newInstance("tes");
        tes.test();
    }
}
