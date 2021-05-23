package com.sproxy.test;

import com.sproxy.method.MethodCallBack;
import com.sproxy.method.MethodFastClass;
import com.sproxy.method.MethodFastClassBuilder;
import com.sproxy.method.MethodProxy;

public class JavaBean$$proxy extends JavaBean {
    public static MethodFastClass methodFastClass;
    private MethodCallBack methodCallBack;
    private MethodProxy proxy1;
    private MethodProxy proxy2;
    private MethodProxy proxy3;

    public JavaBean$$proxy(MethodCallBack methodCallBack) {
        super();
        this.methodCallBack = methodCallBack;
        proxy1 = new MethodProxy(methodFastClass, "test", "()V");
        proxy2 = new MethodProxy(methodFastClass, "d", "(Ljava/lang/String;)V");
        proxy3 = new MethodProxy(methodFastClass, "ggg", "(II)I");
    }

    static {
        methodFastClass = MethodFastClassBuilder.getInstance().create(JavaBean.class);
//        methodFastClass =  new MethodFastClassImpl();
    }


    public void d$proxy(String s) {
        super.d(s);
    }

    public void fasdf$proxy(int s) {
        fas$proxy(s);
    }

    public void fas$proxy(Integer s) {

    }

    public void test$proxy() {
        super.test();
    }


    @Override
    public void d(String s) {

        if (this.methodCallBack != null) {
            this.methodCallBack.invoke(this, new Object[]{s}, proxy2);
        } else {
            super.d(s);
        }

    }


    @Override
    public void test() {
        if (this.methodCallBack != null) {
            this.methodCallBack.invoke(this, new Object[0], proxy1);
        } else {
            super.test();
        }
    }

    @Override
    public void test2(int a, Integer b, boolean c) {
        if (this.methodCallBack != null) {
            this.methodCallBack.invoke(this, new Object[]{a, b, c}, proxy1);
        } else {
            super.test2(a, b, c);
        }
    }

    @Override
    public int t1(int i1, boolean i2, int i3) {
        if (this.methodCallBack != null) {
            return (int) this.methodCallBack.invoke(this, new Object[]{i1, i2, i3}, proxy1);
        } else {
            return super.t1(i1, i2, i3);
        }
    }

    @Override
    public int t2() {
        if (this.methodCallBack != null) {

            return (int) this.methodCallBack.invoke(this, new Object[0], proxy1);
        } else {
            return super.t2();
        }
    }
}
