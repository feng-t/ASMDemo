package com.sproxy.method;

public class MethodProxy {
    private MethodFastClass methodFastClass;
    private Signature sig;

    public MethodProxy(MethodFastClass methodFastClass, String name, String desc) {
        sig = new Signature(name, desc);
        this.methodFastClass = methodFastClass;
    }

    public Object invoke(Object obj, Object[] args) {
        return methodFastClass.invoke(methodFastClass.getIndex(sig), obj, args);
    }
}
