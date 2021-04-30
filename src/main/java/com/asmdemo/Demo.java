package com.asmdemo;


import com.asmdemo.core.ASMCreate;
import com.asmdemo.test.JavaProxy;
import org.objectweb.asm.MethodAdapter;

public class Demo {
    public static void main(String[] args) throws Exception {

        ASMCreate create = new ASMCreate();
        create.setParameters("test0000");
        create.save(true);
        create.removeMethod("test1","()V");
        create.invoke("test1",(mv)-> {
            return mv;
        });
        create.save(true);
        JavaProxy o = (JavaProxy) create.setSuperClass(JavaProxy.class).create();
        o.test1();

    }



}
