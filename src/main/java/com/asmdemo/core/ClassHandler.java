package com.asmdemo.core;

import org.objectweb.asm.ClassAdapter;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

/**
 * 类处理器
 */
public class ClassHandler extends ClassAdapter implements Opcodes {
    private String superClassName;
    /**
     * Constructs a new {@link ClassAdapter} object.
     *
     * @param cv the class visitor to which this adapter must delegate calls.
     */
    public ClassHandler(ClassVisitor cv) {
        super(cv);
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        String enhancedName = name + "$0";
        superClassName=name;
        super.visit(version, access, enhancedName, signature, superClassName, interfaces);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);

        if (mv!=null){
            if (!name.equals("<init>")) {
                mv = new MethodHandler(mv);

            }else {
                return new InitMethodHandler(mv,superClassName);
            }
        }
        return mv;
    }

    public void setParameter(Object... parameters) {

    }
}
