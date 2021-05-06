package com.asmdemo.cglib;

public interface SimpleFastClass<T> {
    Object invoke(T obj,Object[]args);
}
