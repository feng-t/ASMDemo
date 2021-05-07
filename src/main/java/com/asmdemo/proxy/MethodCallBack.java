package com.asmdemo.proxy;

/**
 *
 */
public interface MethodCallBack {
    Object invoke(Object obj,Object[] parameter,MethodProxy proxy);
}
