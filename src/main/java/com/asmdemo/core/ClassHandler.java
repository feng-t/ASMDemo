package com.asmdemo.core;

import org.objectweb.asm.*;

import java.util.Map;

/**
 * 类处理器
 */
public class ClassHandler extends ClassAdapter implements Opcodes {
    private String superClassName;

    private Map<String,MethodBack>handlerMap;
    /**
     * 构造方法参数
     */
    private Object[] parameters;
    /**
     * Constructs a new {@link ClassAdapter} object.
     *
     * @param cv the class visitor to which this adapter must delegate calls.
     */
    public ClassHandler(ClassVisitor cv,Object...parameters) {
        super(cv);
        if (parameters!=null){
            this.parameters=parameters;
        }
    }


    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        String enhancedName = name + "$0";
        superClassName=name;
        super.visit(version, access, enhancedName, signature, superClassName, interfaces);
    }

    @Override
    public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
        return null;
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
        if (mv!=null){
            if (name.equals("<init>")) {
                mv= new InitMethodHandler(mv,superClassName,parameters);
            }else {
                MethodBack back = null;
                if (handlerMap!=null&&(back=handlerMap.get(name))!=null){
                    mv = back.invoke(mv);
                }
            }
        }

        return mv;
    }


    @Override
    public void visitEnd() {

        super.visitEnd();
    }

    public void setMethodHandler(Map<String, MethodBack> handlerMap) {
        this.handlerMap=handlerMap;
    }
}
