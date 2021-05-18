package com.asmdemo.cglib.proxy;

/**
 *
 */
public interface MethodCallBack {
    Object invoke(Object obj,Object[] parameter,MethodProxy proxy);
}
