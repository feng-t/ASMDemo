package com.asmdemo.core;

import org.objectweb.asm.MethodVisitor;

/**
 * 有返回回调方法
 */
public interface MethodBack {
    /**
     * 传入 {@link MethodVisitor}
     * @param handler
     * @return
     */
    MethodVisitor invoke(MethodVisitor handler);
}
