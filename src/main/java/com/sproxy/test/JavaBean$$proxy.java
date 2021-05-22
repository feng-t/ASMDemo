package com.sproxy.test;

import com.sproxy.method.MethodCallBack;
import com.sproxy.method.MethodFastClass;
import com.sproxy.method.MethodFastClassBuilder;
import com.sproxy.method.MethodProxy;

public class JavaBean$$proxy extends JavaBean{
    public static MethodFastClass methodFastClass;
    private  MethodCallBack methodCallBack;
    private MethodProxy proxyTest;
    private MethodProxy proxyD;

    public JavaBean$$proxy(MethodCallBack methodCallBack){
        super();
        this.methodCallBack=methodCallBack;
        proxyTest=new MethodProxy(methodFastClass,"test","()V");
        proxyD=new MethodProxy(methodFastClass,"d","(Ljava/lang/String;)V");
    }
    static {
        methodFastClass=MethodFastClassBuilder.getInstance().create(JavaBean.class);
    }


    public void d$proxy(String s){
        super.d(s);
    }

    public void test$proxy(){
        super.test();
    }

    @Override
    public void d(String s) {
        if (this.methodCallBack!=null){
            this.methodCallBack.invoke(this,new Object[]{s},proxyD);
        }else {
            super.d(s);
        }
    }

    @Override
    public void test() {
        if (this.methodCallBack!=null){
            this.methodCallBack.invoke(this,new Object[0],proxyTest);
        }else {
            super.test();
        }
    }

}
