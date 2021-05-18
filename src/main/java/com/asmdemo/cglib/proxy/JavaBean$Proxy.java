package com.asmdemo.cglib.proxy;

/**
 * 带$符号的表示由ASM生成的
 */
public class JavaBean$Proxy extends JavaBean{
    private MethodFastClass methodFastClass=new MethodFastClass$JavaBean$Proxy();
    private MethodCallBack methodCallBack;

    public void setMethodCallBack(MethodCallBack methodCallBack) {
        this.methodCallBack = methodCallBack;
    }

    public void test1$proxy(){
        super.test1();
    }
    @Override
    public void test1() {
        if (methodCallBack!=null){
            Object invoke = methodCallBack.invoke(this, new Object[0], new MethodProxy(methodFastClass, "test1", "()V"));
        }else {
            super.test1();
        }
    }

    @Override
    public int test3(int a, int b) {
        if (methodCallBack!=null){
            Object invoke = methodCallBack.invoke(this, new Object[]{a,b}, new MethodProxy(methodFastClass, "test3", "(II)I"));
            return (int) invoke;
        }else {
           return super.test3(a,b);
        }
    }
    @Override
    public int test2() {
        if (methodCallBack!=null){
            Object invoke = methodCallBack.invoke(this, new Object[0], new MethodProxy(methodFastClass, "test2", "()I"));
            return (int) invoke;
        }else {
            return super.test2();
        }
    }
    public int test2$proxy() {
        return super.test2();
    }
    public int test3$proxy(int a,int b) {
        return super.test3(a,b);
    }

    public static void main(String[] args) {
        JavaBean$Proxy bean = new JavaBean$Proxy();
        bean.setMethodCallBack((obj, parameter, proxy) -> {
            System.out.println("before");
            Object invoke = proxy.invoke(obj, parameter);
            System.out.println(invoke);
            System.out.println("after");
            return invoke;
        });
        bean.test1();
    }
}
