package com.asmdemo.proxy;

public interface MethodFastClass {
     int getIndex(Signature signature);
    Object invoke(int index, Object obj, Object[] parameter);
}
