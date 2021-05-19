package com.sproxy.utils;

import org.objectweb.asm.Type;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ClassUtils {
    public static String defineSubClassName(String className) {
        return className + "$0";
    }

    public static String defineSubClassPath(String className) {
        String replace = className.replaceAll("\\.", "/");
        if (replace.endsWith("$0.class")) {
            return replace;
        } else if (replace.endsWith(".class")) {
            return replace.replaceAll(".class", "$0.class");
        }
        return replace + "$0.class";
    }

    public static Class<?> getBasisClass(Class<?> c) {
        if (c == Integer.class) {
            return int.class;
        } else if (c == Short.class) {
            return short.class;
        } else if (c == Long.class) {
            return long.class;
        } else if (c == Character.class) {
            return char.class;
        } else if (c == Double.class) {
            return double.class;
        } else if (c == Float.class) {
            return float.class;
        } else if (c == Boolean.class) {
            return boolean.class;
        } else if (c == Byte.class) {
            return byte.class;
        }
        return c;
    }

    public static String getBox(String s) {
        switch (s) {
            case "int":
                return Integer.class.getName();
            case "short":
                return Short.class.getName();
            case "long":
                return Long.class.getName();
            case "char":
                return Character.class.getName();
            case "double":
                return Double.class.getName();
            case "float":
                return Float.class.getName();
            case "boolean":
                return Boolean.class.getName();
            case "byte":
                return Byte.class.getName();
            default:
                return s;
        }
    }

    public static String[] getParameterTypes(String des) {
        Type type = Type.getType(des);
        Type[] types = type.getArgumentTypes();
        String[] boxId = new String[types.length];
        for (int i = 0; i < types.length; i++) {
            boxId[i] = getBox(types[i].getClassName());
        }
        return boxId;
    }

    public static String getReturnDes(String clazz) {

        return null;
    }

    public static void main(String[] args) {
        getParameterTypes("(IIJLjava/lang/Long;B)V");
    }


    public static void saveFile(String name, byte[] bytes) {
        FileOutputStream stream = null;
        try {
            File file = new File(name);
            if (!file.exists()) {
                boolean mkdirs = file.getParentFile().mkdirs();
                boolean newFile = file.createNewFile();
            }
            stream = new FileOutputStream(file);
            stream.write(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}