package com.asmdemo.agent;


import org.objectweb.asm.*;

import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.security.ProtectionDomain;

public class TestTransformer implements ClassFileTransformer {


    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) {
        if (!className.equals(JavaProxy.class.getName().replaceAll("\\.", "/"))) {
            return null;
        }
        ClassReader cr = null;
        try {
            cr = new ClassReader(JavaProxy.class.getName());
        } catch (IOException e) {
            e.printStackTrace();
        }
        ClassWriter cw = new ClassWriter(cr, ClassWriter.COMPUTE_MAXS);
        ClassAdapter ca = new ClassAdapter(cw) {
            @Override
            public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
                MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
                if (name.equals("test1")) {
                    mv = new MethodAdapter(mv) {
                        @Override
                        public void visitInsn(int opcode) {
                            if ((opcode >= Opcodes.IRETURN && opcode <= Opcodes.RETURN)
                                    || opcode == Opcodes.ATHROW) {
                                mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
                                mv.visitLdcInsn("test--结束了");
                                mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V");
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
        System.out.println("返回");
        return cw.toByteArray();
    }


}
