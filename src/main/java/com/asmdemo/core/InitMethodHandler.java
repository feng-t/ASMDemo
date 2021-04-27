package com.asmdemo.core;

import org.objectweb.asm.MethodAdapter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class InitMethodHandler extends MethodAdapter {
    private String superClassName;

    public InitMethodHandler(MethodVisitor mv, String superClassName) {
        super(mv);
        this.superClassName=superClassName;
    }

    @Override
    public void visitMethodInsn(int opcode, String owner, String name, String desc) {
        if (opcode == Opcodes.INVOKESPECIAL && name.equals("<init>")) {
            owner = superClassName;
        }
        super.visitMethodInsn(opcode, owner, name, desc);
    }
}
