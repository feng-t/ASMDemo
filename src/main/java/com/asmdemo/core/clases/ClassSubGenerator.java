package com.asmdemo.core.clases;

import com.asmdemo.core.method.MethodInfo;
import com.asmdemo.core.method.MethodSubGenerator;
import org.objectweb.asm.*;

import java.util.List;

public class ClassSubGenerator extends ClassVisitor {
    public List<MethodInfo> methods;
    public String suffix;
    /**
     * Constructs a new {@link ClassVisitor} object.
     *
     * @param cv the class visitor to which this adapter must delegate calls.
     */
    public ClassSubGenerator(ClassVisitor cv,String suffix, List<MethodInfo> methods) {
        super(Opcodes.ASM9,cv);

        this.suffix=suffix;
        this.methods=methods;
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        super.visit(version, access, name+suffix, signature, name, interfaces);
    }

    @Override
    public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
        return null;
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        MethodInfo info = new MethodInfo(access, name, desc, signature, exceptions);
        methods.add(info);
        return new MethodSubGenerator(info);
    }
}
