package com.sproxy.method;

public class MethodInfo {
    public int access;
    public String name;
    public String descriptor;
    public String signature;
    public String[] exceptions;

    public MethodInfo(int access, String name, String descriptor, String signature, String[] exceptions) {
        this.access = access;
        this.name = name;
        this.descriptor = descriptor;
        this.signature = signature;
        this.exceptions = exceptions;
    }
}
