package com.sproxy.builder;

import com.sproxy.method.MethodCallBack;
import com.sproxy.method.MethodFastClass;
import com.sproxy.method.MethodInfo;
import com.sproxy.method.MethodProxy;
import com.sproxy.test.CreateTest;
import com.sproxy.test.JavaBean;
import com.sproxy.utils.ClassUtils;
import org.objectweb.asm.*;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ClassEnhance {
    /**
     * 生成的类的名字
     */
    protected String builderClassName;
    protected Class<?> proxyClass;
    protected MethodFastClass methodFastClass;
    protected MethodCallBack methodCallBack;
    protected MethodInfo init;
    protected MethodInfo clinit;
    private List<MethodInfo> methodInfos = Collections.synchronizedList(new ArrayList<>());

    public void setMethodCallBack(MethodCallBack methodCallBack) {
        this.methodCallBack = methodCallBack;
    }

    public void setProxyClass(Class<?> proxyClass) {
        this.proxyClass = proxyClass;
    }

//    public Object create(){
//
//
//
//        return new JavaP923();
//    }


    private byte[] create0() throws IOException {
        String name = proxyClass.getName();
        builderClassName=name+"$$proxy";
        ClassReader cr = new ClassReader(proxyClass.getName());
        ClassWriter cw=new ClassWriter(cr,ClassWriter.COMPUTE_MAXS);
        ClassVisitor cv1 = new ClassVisitor(Opcodes.ASM9, cw) {
            @Override
            public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {

                super.visit(version, access, builderClassName.replaceAll("\\.","/"), signature, name, interfaces);
                cv.visitField(Opcodes.ACC_PRIVATE|Opcodes.ACC_STATIC,"callBack",Type.getDescriptor(MethodCallBack.class),null,null);
            }

            @Override
            public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
                if (!name.equals("<init>")&&!name.equals("<clinit>")){
                    methodInfos.add(new MethodInfo(access, name, descriptor, signature, exceptions));
                    MethodVisitor mv = super.visitMethod(access, name, descriptor, signature, exceptions);
                    return mv;
                }
                if (name.equals("<init>")){
                    init=new MethodInfo(access, name, descriptor, signature, exceptions);
                }
                return super.visitMethod(access, name, descriptor, signature, exceptions);
            }
        };
        cr.accept(cv1,ClassWriter.COMPUTE_MAXS);


        return cw.toByteArray();
    }

//    class JavaP923 extends JavaBean {
//        @Override
//        public void test() {
//            if (methodCallBack!=null) {
//                methodCallBack.invoke(this, null, new MethodProxy(methodFastClass, "test", "()V"));
//            }else {
//                super.test();
//            }
//        }
//        public void test$proxy() {
//            super.test();
//        }
//    }

    public static void main(String[] args) throws Exception {
        ClassEnhance enhance = new ClassEnhance();
        enhance.setProxyClass(JavaBean.class);
        byte[] enhance0 = enhance.create0();
        ClassUtils.saveFile("/Users/hu/IdeaProjects/ASMDemo/target/classes/javaBean.class",enhance0);

    }
}
