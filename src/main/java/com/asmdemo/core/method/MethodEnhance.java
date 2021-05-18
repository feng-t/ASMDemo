package com.asmdemo.core.method;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

public class MethodEnhance extends MethodVisitor implements Opcodes{
    private MethodInfo method;
    public Type[] types ;

    /**
     * Constructs a new {@link MethodVisitor} object.
     *
     * @param mv     the code visitor to which this adapter must delegate calls.
     * @param method
     */
    public MethodEnhance(MethodVisitor mv, MethodInfo method) {
        super(Opcodes.ASM9,mv);
        this.method = method;
        types = Type.getArgumentTypes(method.desc);
    }


    @Override
    public void visitCode() {
        if ("<init>".equals(method.name)) {
            super.visitCode();
            return;
        }
        mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
        mv.visitLdcInsn("开始");
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V");
        super.visitCode();
    }

    @Override
    public void visitInsn(int opcode) {
        if ((opcode >= Opcodes.IRETURN && opcode <= Opcodes.RETURN)
                || opcode == Opcodes.ATHROW) {
            if ("test1".equals(method.name)) {
                //方法在返回之前，打印
                mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");

                mv.visitVarInsn(getReturnToLoad(opcode), types.length + 1);

                mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(I)V",false);

                mv.visitVarInsn(getReturnToLoad(opcode), types.length + 1);
            }
        }
        mv.visitInsn(opcode);
    }

    /**
     *
     *             int ILOAD = 21; // visitVarInsn
     *             int LLOAD = 22; // -
     *             int FLOAD = 23; // -
     *             int DLOAD = 24; // -
     *             int ALOAD = 25; // -
     * @param opcode
     * @return
     */
    public static int getReturnToLoad(int opcode){
        if (opcode >= IRETURN && opcode <= ARETURN){
            return opcode-151;
        }
        return 0;
    }
    /**
     * 获取对应的命令
     *     int IRETURN = 172; // visitInsn
     *     int LRETURN = 173; // -
     *     int FRETURN = 174; // -
     *     int DRETURN = 175; // -
     *     int ARETURN = 176; // -
     *
     *     int RETURN = 177; // -
     *
     *     int ISTORE = 54; // visitVarInsn
     *     int LSTORE = 55; // -
     *     int FSTORE = 56; // -
     *     int DSTORE = 57; // -
     *     int ASTORE = 58; // -
     * @param opcode
     * @return
     */
    public static int getReturnTypeToStore(int opcode){
        if (opcode>=IRETURN&&opcode<=ARETURN){
            return opcode-118;
        }
        return 0;
    }
}
