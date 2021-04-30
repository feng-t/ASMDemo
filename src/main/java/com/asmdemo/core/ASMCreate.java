package com.asmdemo.core;

import com.asmdemo.utils.ClassUtils;
import com.sun.deploy.cache.BaseLocalApplicationProperties;
import org.objectweb.asm.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ASMCreate extends ClassLoader implements Opcodes {

    private Class<?> superClass;
    private Class<?> subClass;
    private boolean save = false;
    public Map<String,String> removeMethodList=new HashMap<>();

    private Map<String,MethodBack>handlerMap=new ConcurrentHashMap<>();

    public void save(boolean save) {
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
        Class<?>[] parametersClasses={};
        if (parameters!=null){
            //使用基本类型时可能会出现问题
            parametersClasses = Arrays.stream(parameters).map(Object::getClass).toArray((p)->new Class<?>[parameters.length]);
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
        ClassHandler handler = new ClassHandler(cw, parameters);

        handler.setMethodHandler(handlerMap);
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


    public void invoke(String key, MethodBack back) {
        handlerMap.put(key,back);
    }

    /**
     * 移除方法
     * @param methodName
     * @param desc
     */
    public void removeMethod(String methodName,String desc) {
        removeMethodList.put(methodName,desc);
    }

    //---------------------------------------------------------------------------------------//
    //---------------------------------------------------------------------------------------//
    //---------------------------------------------------------------------------------------//
    /**
     * 类处理器
     */
    class ClassHandler extends ClassAdapter implements Opcodes {
        private String superClassName;

        private Map<String,MethodBack>handlerMap;
        /**
         * 构造方法参数
         */
        private Object[] parameters;;

        /**
         * Constructs a new {@link ClassAdapter} object.
         *
         * @param cv the class visitor to which this adapter must delegate calls.
         */
        public ClassHandler(ClassVisitor cv, Object...parameters) {
            super(cv);
            if (parameters!=null){
                this.parameters=parameters;
            }
        }


        @Override
        public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
            String enhancedName = name + "$0";
            superClassName=name;
            super.visit(version, access, enhancedName, signature, superClassName, interfaces);
        }

        @Override
        public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
            return null;
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
            String s = removeMethodList.get(name);
            if (desc.equals(s)) {
                return null;
            }
            MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
            if (mv!=null){
                if (name.equals("<init>")) {
                    mv= new InitMethodHandler(mv,superClassName,parameters);
                }else {

                    MethodBack back = null;
                    if (handlerMap!=null&&(back=handlerMap.get(name))!=null){
                        mv = back.invoke(mv);
                    }
                    mv=new MethodHandler(mv);
                }
            }

            return mv;
        }


        @Override
        public void visitEnd() {

            super.visitEnd();
        }

        public void setMethodHandler(Map<String, MethodBack> handlerMap) {
            this.handlerMap=handlerMap;
        }
    }

}
