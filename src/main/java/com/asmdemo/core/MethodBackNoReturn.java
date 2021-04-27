package com.asmdemo.core;

/**
 * 无返回回调方法，主要作用是插入到方法的前面或后面
 */
public abstract class MethodBackNoReturn implements MethodBack{
    abstract void invoke();
}
