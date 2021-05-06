package com.asmdemo.cglib;

import com.asmdemo.agent.JavaProxy;

public class SimpleCGlibBean extends JavaProxy {

    private MethodCallBack<SimpleCGlibBean> callBack;

    public void setCallBack(MethodCallBack<SimpleCGlibBean> callBack) {
        this.callBack = callBack;
    }

    private int test1$$Proxy(){
        return super.test1();
    }
    @Override
    public int test1() {
        if (callBack==null){
            return super.test1();
        }
        Object intercept = this.callBack.intercept(this, new Object[0], (obj, args) -> {
            return obj.test1$$Proxy();
        });
        return (int) intercept;
    }

    public static void main(String[] args) {
        SimpleCGlibBean bean = new SimpleCGlibBean();
        bean.setCallBack((obj, args1, fastClass) -> {
            System.out.println("执行前");
            Object invoke = fastClass.invoke(obj, args1);
            System.out.println("执行后"+invoke);
            return invoke;
        });
        bean.test1();
    }
}
