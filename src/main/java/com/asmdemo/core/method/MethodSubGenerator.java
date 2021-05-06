package com.asmdemo.core.method;

import org.objectweb.asm.*;

/**
 * 空方法
 */
public class MethodSubGenerator implements MethodVisitor {
    private MethodInfo info;
    public MethodSubGenerator(MethodInfo info){
        this.info=info;
    }
    @Override
    public AnnotationVisitor visitAnnotationDefault() {
        return null;
    }

    @Override
    public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
        return null;
    }

    @Override
    public AnnotationVisitor visitParameterAnnotation(int parameter, String desc, boolean visible) {
        return null;
    }

    @Override
    public void visitAttribute(Attribute attr) {

    }

    @Override
    public void visitCode() {

    }

    @Override
    public void visitFrame(int type, int nLocal, Object[] local, int nStack, Object[] stack) {

    }

    @Override
    public void visitInsn(int opcode) {
        info.returnCode=opcode;
    }

    @Override
    public void visitIntInsn(int opcode, int operand) {

    }

    @Override
    public void visitVarInsn(int opcode, int var) {

    }

    @Override
    public void visitTypeInsn(int opcode, String type) {

    }

    @Override
    public void visitFieldInsn(int opcode, String owner, String name, String desc) {

    }

    @Override
    public void visitMethodInsn(int opcode, String owner, String name, String desc) {

    }

    @Override
    public void visitJumpInsn(int opcode, Label label) {

    }

    @Override
    public void visitLabel(Label label) {

    }

    @Override
    public void visitLdcInsn(Object cst) {

    }

    @Override
    public void visitIincInsn(int var, int increment) {

    }

    @Override
    public void visitTableSwitchInsn(int min, int max, Label dflt, Label[] labels) {

    }

    @Override
    public void visitLookupSwitchInsn(Label dflt, int[] keys, Label[] labels) {

    }

    @Override
    public void visitMultiANewArrayInsn(String desc, int dims) {

    }

    @Override
    public void visitTryCatchBlock(Label start, Label end, Label handler, String type) {

    }

    @Override
    public void visitLocalVariable(String name, String desc, String signature, Label start, Label end, int index) {

    }

    @Override
    public void visitLineNumber(int line, Label start) {

    }

    @Override
    public void visitMaxs(int maxStack, int maxLocals) {

    }

    @Override
    public void visitEnd() {
    }
}
