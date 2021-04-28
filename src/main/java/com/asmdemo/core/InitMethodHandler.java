package com.asmdemo.core;

import com.asmdemo.utils.StringUtils;
import org.objectweb.asm.MethodAdapter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;


public class InitMethodHandler extends MethodAdapter {
    private String superClassName;
    /**
     * 构造方法参数
     */
    private Object[] parameters = {};

    public InitMethodHandler(MethodVisitor mv, String superClassName, Object... parameters) {
        super(mv);
        this.superClassName = superClassName;
        if (parameters != null && parameters.length != 0) {
            this.parameters = parameters;
        }
    }



    @Override
    public void visitMethodInsn(int opcode, String owner, String name, String desc) {
        if (opcode == Opcodes.INVOKESPECIAL && name.equals("<init>")) {
            owner = superClassName;
        }
        if (parameters != null && parameters.length != 0) {
            StringBuilder sb = new StringBuilder();
            sb.append("(");
            for (Object parameter : parameters) {
                sb.append(Type.getDescriptor(parameter.getClass()));
            }
            sb.append(")V");
            desc = sb.toString();
            for (int i = 0; i < parameters.length; i++) {
                mv.visitVarInsn(Opcodes.ALOAD,i+1);
            }
        }
        //标志符，父类，方法名，参数
        super.visitMethodInsn(opcode, owner, name, desc);
    }

    @Override
    public void visitCode() {
        super.visitCode();
    }

    @Override
    public void visitEnd() {
        super.visitEnd();
//        mv.visitVarInsn(Opcodes.ALOAD, 0);
//        mv.visitIntInsn(Opcodes.BIPUSH, 99);
//        mv.visitFieldInsn(Opcodes.PUTFIELD, "com/asmdemo/test/JavaProxy", "yy", "I");

    }
}
