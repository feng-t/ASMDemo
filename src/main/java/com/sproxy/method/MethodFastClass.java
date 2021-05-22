package com.sproxy.method;

public interface MethodFastClass {
    int getIndex(String signature);
    Object invoke(int index, Object obj, Object[] parameter);
}
