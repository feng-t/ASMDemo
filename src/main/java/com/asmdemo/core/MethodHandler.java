package com.asmdemo.core;

import org.objectweb.asm.MethodAdapter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class MethodHandler extends MethodAdapter implements Opcodes {
    /**
     * Constructs a new {@link MethodAdapter} object.
     *
     * @param mv the code visitor to which this adapter must delegate calls.
     */
    public MethodHandler(MethodVisitor mv) {
        super(mv);
    }

    @Override
    public void visitCode() {

        super.visitCode();
    }

    @Override
    public void visitEnd() {
        super.visitEnd();
    }
}
