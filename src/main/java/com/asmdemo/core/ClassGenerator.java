package com.asmdemo.core;

import com.asmdemo.core.clases.ClassSubGenerator;
import com.asmdemo.core.method.MethodEnhance;
import com.asmdemo.core.method.MethodInfo;
import com.asmdemo.test.JavaProxy;
import org.objectweb.asm.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ClassGenerator extends ClassLoader implements Opcodes {


    public static void main(String[] args) throws Exception {
        ClassGenerator generator = new ClassGenerator();
        generator.save(true);
        generator.setSuperClass(JavaProxy.class);
        JavaProxy o = (JavaProxy) generator.create();
        int i = o.test1();
    }

    private String suffix = "$temp";
    private List<MethodInfo> methods = new ArrayList<>();


    private ClassReader cr;
    private ClassWriter cw;
    private ClassSubGenerator generator;
    private Class<?>[] parametersClass = {};
    private Class<?> superClass;
    private Class<?> subClass;
    private boolean save = false;

    public void save(boolean save) {
        this.save = save;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public void setSuperClass(Class<?> superClass) {
        this.superClass = superClass;
    }


    /**
     * 创建代理类
     */
    public Object create(Object... parameters) throws IOException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {

        if (subClass == null) {
            parametersClass = Arrays.stream(parameters).map(Object::getClass).toArray((p) -> new Class<?>[parameters.length]);
            loadClass();
        }

        if (parameters.length == 0) {
            return subClass.newInstance();
        }
        Constructor<?> constructor = subClass.getConstructor(parametersClass);
        return constructor.newInstance(parameters);
    }

    public byte[] getByte() throws IOException {
        cr = new ClassReader(superClass.getName());
        cw = new ClassWriter(cr, ClassWriter.COMPUTE_MAXS);
        generator = new ClassSubGenerator(cw, suffix, methods);
        cr.accept(generator, ClassReader.SKIP_DEBUG);
        
        for (MethodInfo method : methods) {
            Type[] types = Type.getArgumentTypes(method.desc);
            MethodEnhance mv = new MethodEnhance(cw.visitMethod(method.access, method.name, method.desc, method.signature, method.exceptions), method);
            mv.visitCode();
            mv.visitVarInsn(ALOAD, 0);
            for (int i = 0; i < types.length; i++) {
                mv.visitVarInsn(ALOAD, i + 1);
            }
            System.out.println(method.name + ":" + method.returnCode);
            mv.visitMethodInsn(INVOKESPECIAL, superClass.getName().replaceAll("\\.", "/"), method.name, method.desc);
            if (method.returnCode != Opcodes.RETURN && method.name.equals("test1")) {
                mv.visitVarInsn(MethodEnhance.getReturnTypeToStore(method.returnCode), types.length + 1);
            }
            mv.visitInsn(method.returnCode);
            mv.visitMaxs(types.length + 1, types.length + 1);
            mv.visitEnd();
        }
        if (save) {
            String path = Thread.currentThread().getContextClassLoader().getResource(".").getPath()
                    + superClass.getName().replaceAll("\\.", "/") + suffix + ".class";

            File file = new File(path);
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                file.createNewFile();
            }
            FileOutputStream stream = new FileOutputStream(file);
            stream.write(cw.toByteArray());
            stream.close();
            System.out.println("文件保存路径为："+path);
        }
        return cw.toByteArray();
    }
    private void loadClass() throws IOException {
        if (cr!=null&&cw!=null&&generator!=null&&subClass!=null){
            System.out.println("直接退出");
            return;
        }
        byte[] bytes = getByte();
        subClass = this.defineClass(superClass.getName() + suffix, bytes, 0, bytes.length);
    }

}
