package com.asmdemo.core;

import com.asmdemo.test.JavaProxy;
import com.asmdemo.utils.ClassUtils;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class ASMCreate extends ClassLoader implements Opcodes {

    private Class<?> superClass;
    private Class<?> subClass;
    private boolean save = false;

    public void setSave(boolean save) {
        this.save = save;
    }

    /**
     * 构造方法参数
     */
    private Object[] parameters = {};

    /**
     * 设置父类
     *
     * @param superClass
     * @return
     */
    public ASMCreate setSuperClass(Class<?> superClass) {
        this.superClass = superClass;
        return this;
    }

    /**
     * 设置构造方法参数
     *
     * @param parameters
     * @return
     */
    public ASMCreate setParameters(Object... parameters) {
        this.parameters = parameters;
        return this;
    }


    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {

        if (superClass==null||!name.equals(ClassUtils.defineSubClassName(superClass.getName()))) {
            return super.loadClass(name);
        }
        try {
            return getSubClass();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
//        return super.loadClass(name);
    }

    public Object create() throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, ClassNotFoundException {
        if (superClass==null){
            throw new RuntimeException("执行create之前必须执行setSuperClass");
        }
        if (subClass==null){
            subClass= this.loadClass(ClassUtils.defineSubClassName(superClass.getName()));
        }
        Class<?>[] parametersClasses = new Class<?>[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            parametersClasses[i] = parameters[i].getClass();
        }
        Constructor<?> constructor = subClass.getConstructor(parametersClasses);
        return constructor.newInstance(parameters);
    }

    /**
     * 创建代理类
     */
    private Class<?> getSubClass() throws IOException {
        if (this.superClass == null) {
            throw new RuntimeException("在调用create方法之前应该先调用setSuperClass");
        }
        ClassReader cr = new ClassReader(superClass.getName());
        ClassWriter cw = new ClassWriter(cr, ClassWriter.COMPUTE_MAXS);
        ClassHandler handler = new ClassHandler(cw);
        handler.setParameter(parameters);
        cr.accept(handler, ClassReader.SKIP_DEBUG);
        if (save) {
            File file = new File(Thread.currentThread().getContextClassLoader().getResource(".").getPath()
                    + ClassUtils.defineSubClassPath(superClass.getName()));
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                file.createNewFile();
            }
            FileOutputStream stream = new FileOutputStream(file);
            stream.write(cw.toByteArray());
            stream.close();
        }
        byte[] bytes = cw.toByteArray();
        subClass = this.defineClass(ClassUtils.defineSubClassName(superClass.getName()), bytes, 0, bytes.length);
        return subClass;
    }




}
