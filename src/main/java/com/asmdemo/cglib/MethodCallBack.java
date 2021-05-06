package com.asmdemo.cglib;

public interface MethodCallBack<T> {

    Object intercept(T obj,Object[]args,SimpleFastClass<T> fastClass);
}
