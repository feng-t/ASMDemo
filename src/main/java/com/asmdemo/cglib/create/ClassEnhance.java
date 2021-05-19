package com.asmdemo.cglib.create;

import com.asmdemo.cglib.proxy.JavaBean;
import com.asmdemo.cglib.proxy.MethodCallBack;
import com.asmdemo.cglib.proxy.MethodFastClass;
import com.asmdemo.cglib.proxy.MethodProxy;
import jdk.internal.org.objectweb.asm.Opcodes;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;

import java.io.IOException;

public class ClassEnhance implements Opcodes {
    protected Class<?> proxyClass;
    protected MethodFastClass methodFastClass;
    protected MethodCallBack methodCallBack;

    public void setSuperClass(Class<?> proxyClass) {
        this.proxyClass = proxyClass;
    }

    public void setMethodCallBack(MethodCallBack methodCallBack) {
        this.methodCallBack = methodCallBack;
    }

    public Object create(){
        return new JavaBean_proxy();
    }

    /**
     * 创建代理类
     */
    private void create0() throws IOException {
        ClassReader cr = new ClassReader(proxyClass.getName());
        ClassWriter cw = new ClassWriter(cr, ClassWriter.COMPUTE_MAXS);
    }
    class JavaBean_proxy extends JavaBean{
        @Override
        public void test1() {

            methodCallBack.invoke(this,new Object[0],new MethodProxy(methodFastClass,"test1","()V"));
        }
    }

    public static void main(String[] args) {
        ClassEnhance enhance = new ClassEnhance();
        Object o = enhance.create();
    }
}
