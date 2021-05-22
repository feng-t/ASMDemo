package com.sproxy.method;

public class MethodProxy {
    private MethodFastClass methodFastClass;
    private Signature sig;
    public String methodInfo(){
        return sig.toString();
    }

    public MethodProxy(MethodFastClass methodFastClass, String name, String desc) {
        sig = new Signature(name, desc);
        this.methodFastClass = methodFastClass;
    }

    public Object invoke(Object obj, Object[] args) {
        int index = methodFastClass.getIndex(sig.toString());
        return methodFastClass.invoke(index, obj, args);
    }
}
