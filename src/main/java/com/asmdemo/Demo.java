package com.asmdemo;


import com.asmdemo.core.ASMCreate;
import com.asmdemo.test.JavaProxy;
import org.objectweb.asm.MethodAdapter;

public class Demo {
    public static void main(String[] args) throws Exception {

        ASMCreate create = new ASMCreate();
        create.setParameters("test0000");
        create.invoke("test1",(mv)-> new MethodAdapter(mv){
            @Override
            public void visitMethodInsn(int opcode, String owner, String name, String desc) {
                super.visitMethodInsn(opcode, owner, name, desc);
            }
        });
        create.save(true);
        JavaProxy o = (JavaProxy) create.setSuperClass(JavaProxy.class).create();
        o.test1();

    }



}
