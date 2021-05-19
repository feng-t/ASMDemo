package com.asmdemo.agent;

import org.objectweb.asm.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ProxyBean extends ClassLoader {



    public static void main(String[] args) throws IOException, InstantiationException, IllegalAccessException {
        new ProxyBean().run();
    }

    public void run() throws IOException, InstantiationException, IllegalAccessException {
        ClassReader cr = new ClassReader(JavaProxy.class.getName());
        ClassWriter cw = new ClassWriter(cr, ClassWriter.COMPUTE_MAXS);
        ClassVisitor ca = new ClassVisitor(Opcodes.ASM9,cw) {

            @Override
            public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
                MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
                if (name.equals("test1")) {
                    mv = new MethodVisitor(Opcodes.ASM9,mv) {
                        @Override
                        public void visitInsn(int opcode) {
                            if ((opcode >= Opcodes.IRETURN && opcode <= Opcodes.RETURN)
                                    || opcode == Opcodes.ATHROW) {
                                mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
                                mv.visitLdcInsn("test--结束了");
                                mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V",false);
                            }
                            mv.visitInsn(opcode);
                        }
                    };
                }
                return mv;
            }
        };
        cr.accept(ca, ClassReader.SKIP_DEBUG);
        byte[] bytes = cw.toByteArray();

        String path = Thread.currentThread().getContextClassLoader().getResource(".").getPath()
                +JavaProxy.class.getName().replaceAll("\\.", "/") + "_ppp" + ".class";

        File file = new File(path);
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            file.createNewFile();
        }
        FileOutputStream stream = new FileOutputStream(file);
        stream.write(cw.toByteArray());
        stream.close();
        System.out.println("文件保存路径为："+path);


        Class<?> defineClass = this.defineClass(JavaProxy.class.getName(), bytes, 0, bytes.length);
        JavaProxy o = (JavaProxy) defineClass.newInstance();
        o.test1();
    }

}
