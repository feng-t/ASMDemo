package com.asmdemo.core;

import com.asmdemo.test.JavaProxy;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

class ASMCreateTest {

    @Test
    public void create() throws IOException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {

        ASMCreate create = new ASMCreate();
        create.setParameters("s");
        create.save(true);
        JavaProxy o = (JavaProxy) create.setSuperClass(JavaProxy.class).create();
        o.test1();
    }
}