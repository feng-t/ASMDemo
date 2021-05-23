package com.sproxy.builder;

import com.sproxy.method.MethodCallBack;
import com.sproxy.method.MethodFastClass;
import com.sproxy.method.MethodInfo;
import com.sproxy.test.Bean1;
import com.sproxy.test.JavaBean;
import com.sproxy.utils.ClassUtils;
import com.sproxy.utils.MethodUtils;
import com.sun.org.apache.bcel.internal.generic.ALOAD;
import org.objectweb.asm.*;
import org.objectweb.asm.tree.analysis.Frame;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

import static com.sun.jmx.snmp.ThreadContext.contains;

public class ClassEnhance implements Opcodes {
    private Class<?> proxyClass;
    private Class<?> targetClass;
    private MethodCallBack callBack;
    private List<String> methodInfos = Collections.synchronizedList(new ArrayList<>());
    private List<MethodInfo> createMethods = Collections.synchronizedList(new ArrayList<>());
    private byte[] bytes;
    private String subClassName;
    private CustomClassLoader loader=CustomClassLoader.getInstance();



    public void setProxyClass(Class<?> proxyClass) {

        this.proxyClass = proxyClass;
    }

    public void setCallBack(MethodCallBack callBack) {
        this.callBack = callBack;
    }

    public Object create(Object... objects) {
        Object obj = null;
        try {
            if (bytes == null || targetClass == null) {
                bytes = createProxyClass();
                targetClass = loader.findClass(this.subClassName, bytes);
            }
            Class<?>[] classes = new Class<?>[objects.length+1];
            classes[0]=MethodCallBack.class;
            for (int i = 1; i < objects.length; i++) {
                Class<?> c = objects[i - 1].getClass();
                if (c.isPrimitive()) {

                }
            }
            Constructor<?> constructor = targetClass.getConstructor(classes);
            obj = constructor.newInstance(objects);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return obj;
    }

    public String getSubClassName() {
        return subClassName;
    }

    private byte[] createProxyClass() throws IOException {
        String suffix = ClassUtils.uuid();
//        String suffix = "$$proxy5";
        final String proxyName = Type.getInternalName(this.proxyClass) + suffix;
        this.subClassName = proxyName.replaceAll("/", ".");
        String className = this.proxyClass.getName();

        ClassReader cr = new ClassReader(className);
        ClassWriter cw = new ClassWriter(cr, ClassWriter.COMPUTE_MAXS);
        ClassVisitor visitor = new ClassVisitor(Opcodes.ASM9, cw) {
            @Override
            public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
                super.visit(version, access, proxyName, signature, name, interfaces);
            }

            @Override
            public FieldVisitor visitField(int access, String name, String descriptor, String signature, Object value) {
                return null;
            }

            @Override
            public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
                //System.out.println(access + "\t" + name + "\t" + descriptor + "\t" + signature + "\t" + exceptions);
                if (!name.equals("<clinit>")) {
                    createMethods.add(new MethodInfo(access, name, descriptor, signature, exceptions));
                    if (!name.equals("<init>")) {
                        methodInfos.add(name + "|" + descriptor);
                    }
                }
                return null;
            }
        };
        cr.accept(visitor, ClassReader.SKIP_DEBUG);
        FieldVisitor fv;
        {
            fv = cw.visitField(Opcodes.ACC_PUBLIC + Opcodes.ACC_STATIC, "methodFastClass", "Lcom/sproxy/method/MethodFastClass;", null, null);
            fv.visitEnd();
        }
        {
            fv = cw.visitField(Opcodes.ACC_PRIVATE, "methodCallBack", "Lcom/sproxy/method/MethodCallBack;", null, null);
            fv.visitEnd();
        }
        MethodVisitor mv;
        {
            mv = cw.visitMethod(Opcodes.ACC_STATIC, "<clinit>", "()V", null, null);
            mv.visitCode();
            Label l0 = new Label();
            mv.visitLabel(l0);
            mv.visitMethodInsn(INVOKESTATIC, "com/sproxy/method/MethodFastClassBuilder", "getInstance", "()Lcom/sproxy/method/MethodFastClassBuilder;", false);
            mv.visitLdcInsn(Type.getType(this.proxyClass));
            mv.visitLdcInsn(proxyName);
            mv.visitMethodInsn(INVOKEVIRTUAL, "com/sproxy/method/MethodFastClassBuilder", "create", "(Ljava/lang/Class;Ljava/lang/String;)Lcom/sproxy/method/MethodFastClass;", false);
            mv.visitFieldInsn(PUTSTATIC, proxyName, "methodFastClass", Type.getDescriptor(MethodFastClass.class));
            Label l1 = new Label();
            mv.visitLabel(l1);
            mv.visitLineNumber(26, l1);
            mv.visitInsn(RETURN);
            mv.visitMaxs(2, 0);
            mv.visitEnd();
        }
        Map<MethodInfo, Integer> map = new HashMap<>();
        for (int i = 0; i < createMethods.size(); i++) {
            MethodInfo info = createMethods.get(i);
            if (!info.name.equals("<init>")) {
                //创建MethodProxy
                fv = cw.visitField(ACC_PRIVATE, "proxy" + i, "Lcom/sproxy/method/MethodProxy;", null, null);
                fv.visitEnd();
                map.put(info, i);
            }
        }
        for (MethodInfo info : createMethods) {
            String descriptor = info.descriptor;
            if (info.name.equals("<init>")) {
                descriptor = descriptor.replaceAll("\\(", "(" + Type.getType(MethodCallBack.class).getDescriptor());
                mv = cw.visitMethod(info.access, info.name, descriptor, info.signature, info.exceptions);
                mv.visitCode();
                mv.visitVarInsn(Opcodes.ALOAD, 0);
                Type type = Type.getType(descriptor);
                Type[] types = type.getArgumentTypes();
                for (int i = 1; i < types.length; i++) {
                    Type t = types[i];
                    mv.visitVarInsn(ClassUtils.getVarInst(t.getInternalName()), i + 1);
                }
//                mv.visitTypeInsn(NEW, "com/sproxy/method/MethodProxy");
                mv.visitMethodInsn(Opcodes.INVOKESPECIAL, className.replaceAll("\\.", "/"), info.name, info.descriptor, false);

                mv.visitVarInsn(Opcodes.ALOAD, 0);
                mv.visitVarInsn(Opcodes.ALOAD, 1);
                mv.visitFieldInsn(PUTFIELD, proxyName, "methodCallBack", "Lcom/sproxy/method/MethodCallBack;");
                for (Map.Entry<MethodInfo, Integer> entry : map.entrySet()) {
                    MethodInfo value = entry.getKey();
                    mv.visitVarInsn(ALOAD, 0);
                    mv.visitTypeInsn(NEW, "com/sproxy/method/MethodProxy");
                    mv.visitInsn(DUP);
                    mv.visitFieldInsn(GETSTATIC, proxyName, "methodFastClass", "Lcom/sproxy/method/MethodFastClass;");
                    mv.visitLdcInsn(value.name);
                    mv.visitLdcInsn(value.descriptor);
                    mv.visitMethodInsn(INVOKESPECIAL, "com/sproxy/method/MethodProxy", "<init>", "(Lcom/sproxy/method/MethodFastClass;Ljava/lang/String;Ljava/lang/String;)V", false);
                    mv.visitFieldInsn(PUTFIELD, proxyName, "proxy" + entry.getValue(), "Lcom/sproxy/method/MethodProxy;");
                }
                mv.visitInsn(RETURN);
                mv.visitMaxs(1, 1);
                mv.visitEnd();

            } else {
                mv = cw.visitMethod(ACC_PUBLIC, info.name + "$proxy", info.descriptor, info.signature, info.exceptions);
                mv.visitCode();
                mv.visitVarInsn(Opcodes.ALOAD, 0);
                Type type = Type.getType(info.descriptor);
                Type[] types = type.getArgumentTypes();
                for (int i = 0; i < types.length; i++) {
                    Type t = types[i];
                    mv.visitVarInsn(ClassUtils.getVarInst(t.getInternalName()), i + 1);
                }
                mv.visitMethodInsn(Opcodes.INVOKESPECIAL, className.replaceAll("\\.", "/"), info.name, info.descriptor, false);
                mv.visitInsn(MethodUtils.getReturnOpcode(Type.getType(info.descriptor)));
                mv.visitMaxs(1, 1);
                mv.visitEnd();
                {
                    // 加强方法
                    mv = cw.visitMethod(info.access, info.name, info.descriptor, info.signature, info.exceptions);
                    mv.visitCode();

                    mv.visitVarInsn(ALOAD, 0);
                    mv.visitFieldInsn(GETFIELD, proxyName, "methodCallBack", "Lcom/sproxy/method/MethodCallBack;");
                    Label l1 = new Label();
                    mv.visitJumpInsn(IFNULL, l1);

                    Label l2 = new Label();
                    Label l3 = null;

                    mv.visitLabel(l2);
                    mv.visitVarInsn(ALOAD, 0);
                    mv.visitFieldInsn(GETFIELD, proxyName, "methodCallBack", "Lcom/sproxy/method/MethodCallBack;");
                    mv.visitVarInsn(ALOAD, 0);
                    mv.visitIntInsn(SIPUSH, types.length);
                    //创建数组
                    mv.visitTypeInsn(ANEWARRAY, "java/lang/Object");
                    for (int i = 0; i < types.length; i++) {
                        Type t = types[i];
                        mv.visitInsn(DUP);
                        mv.visitIntInsn(SIPUSH, i);
                        mv.visitVarInsn(MethodUtils.getVarInst(t.getDescriptor()), i + 1);
                        String td = t.getDescriptor();
                        if (!td.contains("L")) {
                            String s = ClassUtils.getBox(t.getClassName()).replaceAll("\\.", "/");
                            mv.visitMethodInsn(INVOKESTATIC, s, "valueOf", "(" + td + ")L" + s + ";", false);
                            System.out.println(s + "\t" + "valueOf" + "\t" + "(" + td + ")L" + s + ";");
                        }
                        //mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
                        mv.visitInsn(AASTORE);
                    }
                    mv.visitVarInsn(ALOAD, 0);
                    mv.visitFieldInsn(GETFIELD, proxyName, "proxy" + map.get(info), "Lcom/sproxy/method/MethodProxy;");
                    mv.visitMethodInsn(INVOKEINTERFACE, "com/sproxy/method/MethodCallBack", "invoke", "(Ljava/lang/Object;[Ljava/lang/Object;Lcom/sproxy/method/MethodProxy;)Ljava/lang/Object;", true);

                    if (!info.descriptor.endsWith("V")) {
                        String basis = type.getReturnType().getClassName();
                        String internal = ClassUtils.getBox(basis).replaceAll("\\.", "/");
                        mv.visitTypeInsn(CHECKCAST, internal);
                        if (!basis.contains(".")) {
                            mv.visitMethodInsn(INVOKEVIRTUAL, internal, basis + "Value", "()" + type.getReturnType(), false);
                        }
                        mv.visitInsn(MethodUtils.getReturnOpcode(Type.getType(info.descriptor)));
                    } else {
                        mv.visitInsn(POP);
                        l3 = new Label();
                        mv.visitJumpInsn(GOTO, l3);
                    }
                    mv.visitLabel(l1);
                    mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
                    mv.visitVarInsn(ALOAD, 0);
                    for (int i = 0; i < types.length; i++) {
                        Type t = types[i];
                        mv.visitVarInsn(ClassUtils.getVarInst(t.getInternalName()), i + 1);
                    }
                    mv.visitMethodInsn(INVOKESPECIAL, className.replaceAll("\\.", "/"), info.name, info.descriptor, false);
                    mv.visitInsn(MethodUtils.getReturnOpcode(Type.getType(info.descriptor)));

                    if (l3 != null) {
                        mv.visitLabel(l3);
                        mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
                        mv.visitInsn(RETURN);
                    }
                    mv.visitMaxs(1, 1);
                    mv.visitEnd();
                }


            }
        }
        return cw.toByteArray();
    }

    public static void main(String[] args) throws IOException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        ClassEnhance enhance = new ClassEnhance();
        enhance.setProxyClass(JavaBean.class);
        byte[] proxyClass = enhance.createProxyClass();
        ClassUtils.saveFile("/Users/hu/IdeaProjects/ASMDemo/target/classes/proxy.class", proxyClass);
        Class<?> aClass = enhance.loader.findClass(enhance.getSubClassName(), proxyClass);
        Constructor<?> constructor = aClass.getConstructor(MethodCallBack.class);

        MethodCallBack callBack = (o, arg, m) -> {
            System.out.println("调用前");
            Object invoke = m.invoke(o, arg);
            System.out.println("获得结果：" + invoke);
            return invoke;
        };
        JavaBean instance = (JavaBean) constructor.newInstance(callBack);
        instance.d("sdfasd");
    }


}
