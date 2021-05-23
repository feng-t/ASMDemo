package com.sproxy.builder;

public class CustomClassLoader extends ClassLoader{
    private static CustomClassLoader classEnhance = new CustomClassLoader();
    private CustomClassLoader(){}

    public static CustomClassLoader getInstance() {
        return classEnhance;
    }
    public Class<?> findClass(String name, byte[] bytes) {
        return defineClass(name, bytes, 0, bytes.length);
    }
}
