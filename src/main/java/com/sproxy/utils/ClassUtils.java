package com.sproxy.utils;

import org.objectweb.asm.Type;

import java.io.*;
import java.util.UUID;

public class ClassUtils {

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

    public static Class<?> getBoxType(Class<?>c){
        String s = c.getName();
        switch (s) {
            case "int":
                return Integer.class;
            case "short":
                return Short.class;
            case "long":
                return Long.class;
            case "char":
                return Character.class;
            case "double":
                return Double.class;
            case "float":
                return Float.class;
            case "boolean":
                return Boolean.class;
            case "byte":
                return Byte.class;
            default:
                return c;
        }
    }
    public static String getBox(Class<?> s){
        String name = s.getName();
        return getBox(name);
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



    public static String uuid(){
        return UUID.randomUUID().toString().substring(0,5);
    }

    public static byte[] getFile(String filename) throws IOException {
        File f = new File(filename);
        if (!f.exists()) {
            throw new FileNotFoundException(filename);
        }

        ByteArrayOutputStream bos = new ByteArrayOutputStream((int) f.length());
        BufferedInputStream in = null;
        try {
            in = new BufferedInputStream(new FileInputStream(f));
            int buf_size = 1024;
            byte[] buffer = new byte[buf_size];
            int len = 0;
            while (-1 != (len = in.read(buffer, 0, buf_size))) {
                bos.write(buffer, 0, len);
            }
            return bos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        } finally {
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            bos.close();
        }
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