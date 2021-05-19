package com.sproxy.method;

public interface MethodFastClass {
    int getIndex(Signature signature);
    Object invoke(int index, Object obj, Object[] parameter);
}
