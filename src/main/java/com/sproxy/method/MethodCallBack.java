package com.sproxy.method;

public interface MethodCallBack {
    Object invoke(Object obj,Object[] parameter,MethodProxy proxy);
}
