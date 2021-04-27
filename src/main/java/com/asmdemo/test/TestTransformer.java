package com.asmdemo.test;


import com.asmdemo.core.ASMCreate;

import java.lang.instrument.ClassFileTransformer;
import java.security.ProtectionDomain;

public class TestTransformer implements ClassFileTransformer {


    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) {
        if (!className.equals(JavaProxy.class.getName().replaceAll("\\.", "/"))) {
            return null;
        }
//        ClassReader cr = new ClassReader(classfileBuffer);
//        ClassWriter cw = new ClassWriter(cr, ClassWriter.COMPUTE_MAXS);
        ASMCreate create = new ASMCreate();

        try {
            create.loadClass(className);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return classfileBuffer;
//        return classfileBuffer;
    }


}
