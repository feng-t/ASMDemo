package com.asmdemo.cglib.proxy;

public class MethodFastClassImpl implements MethodFastClass {
    @Override
    public int getIndex(Signature signature) {

        return 0;
    }

    @Override
    public Object invoke(int index, Object obj, Object[] parameter) {
        return null;
    }
}
