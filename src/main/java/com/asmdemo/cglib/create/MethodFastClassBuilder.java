package com.asmdemo.cglib.create;

import com.asmdemo.agent.JavaProxy;
import com.asmdemo.cglib.proxy.JavaBean;
import com.asmdemo.cglib.proxy.MethodFastClass;
import com.asmdemo.utils.ClassUtils;
import com.asmdemo.utils.FileUtils;
import com.asmdemo.utils.MethodUtils;
import org.objectweb.asm.*;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 创建一个fastclass
 */
public class MethodFastClassBuilder extends ClassLoader implements Opcodes {
    private Class<?> proxyClass;
    private String proxyName;
    private Map<Integer, String> methodToIndex = new ConcurrentHashMap<>();
    /**
     * 方法描述
     */
    private List<String> methodDescriptors = Collections.synchronizedList(new ArrayList<>());

    public MethodFastClassBuilder(Class<?> proxyClass, String proxyName) throws IOException {
        this.proxyName = proxyName;//.replaceAll("\\.", "/") + "$proxy" + (UUID.randomUUID().toString().substring(0, 5));
        this.proxyClass = proxyClass;
        processMethod();
    }

    public String getProxyName() {
        return proxyName;
    }

    /**
     * @throws IOException
     */
    private void processMethod() throws IOException {
        ClassReader cr = new ClassReader(proxyClass.getName());
        ClassWriter cw = new ClassWriter(cr, 0);
        ClassVisitor cv = new ClassVisitor(ASM9, cw) {
            @Override
            public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
                if (!name.equals("<init>")) {
                    methodDescriptors.add(name + "|" + descriptor);
                    System.out.println("代理方法："+name+descriptor);
                }
                return super.visitMethod(access, name, descriptor, signature, exceptions);
            }
        };
        cr.accept(cv, 0);
    }

    public byte[] create() {
        String name = this.proxyName.replaceAll("\\.", "/") + "$proxy$MethodFastClass";
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        cw.visit(V1_8, ACC_PUBLIC | ACC_SUPER, name, null, "java/lang/Object", new String[]{Type.getInternalName(MethodFastClass.class)});
        MethodVisitor mv=MethodUtils.createInit(cw);
        {
            //getIndex
            mv = cw.visitMethod(ACC_PUBLIC, "getIndex", "(Ljava/lang/String;)I", null, null);
            mv.visitCode();
            mv.visitVarInsn(ALOAD, 1);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "hashCode", "()I", false);
            mv.visitVarInsn(ISTORE, 2);
            mv.visitVarInsn(ILOAD, 2);

            Label[] labels = new Label[methodDescriptors.size()];
            int[] keys = new int[methodDescriptors.size()];
            for (int i = 0; i < labels.length; i++) {
                labels[i] = new Label();
            }
            Label defaultLabel = new Label();
            for (int i = 0; i < methodDescriptors.size(); i++) {
                String s = methodDescriptors.get(i);
                keys[i] = s.replaceAll("\\|", "").hashCode();
                methodToIndex.put(keys[i], s);
            }
            Arrays.sort(keys);
            mv.visitLookupSwitchInsn(defaultLabel, keys, labels);
            for (int i = 0; i < labels.length; i++) {

                mv.visitLabel(labels[i]);
                if (i == 0) {
                    mv.visitFrame(F_APPEND, 1, new Object[]{INTEGER}, 0, null);
                    mv.visitInsn(ICONST_0);
                    mv.visitInsn(IRETURN);
                } else {
                    mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
                    if (i <= 5) {
                        mv.visitInsn(ICONST_0 + i);
                    } else {
                        mv.visitIntInsn(SIPUSH, i);
                    }
                    mv.visitInsn(IRETURN);
                }
            }
            mv.visitLabel(defaultLabel);
            mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
            mv.visitInsn(ICONST_M1);
            mv.visitInsn(IRETURN);
            //随意
            mv.visitMaxs(1, 1);
            mv.visitEnd();
        }
        {
            mv = cw.visitMethod(ACC_PUBLIC, "invoke", "(ILjava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;", null, null);
            mv.visitCode();
            mv.visitVarInsn(ALOAD, 2);
            mv.visitTypeInsn(CHECKCAST, this.proxyName);
            mv.visitVarInsn(ASTORE, 4);
//

            Label[] labels = new Label[methodDescriptors.size()];
            for (int i = 0; i < labels.length; i++) {
                labels[i] = new Label();
            }
            Label defaultLabel = new Label();
            mv.visitVarInsn(ILOAD, 1);
            mv.visitTableSwitchInsn(0, labels.length - 1, defaultLabel, labels);
            for (int i = 0; i < labels.length; i++) {
                mv.visitLabel(labels[i]);
                String[] methodInfo = methodDescriptors.get(i).split("\\|");
                if (i == 0) {
                    mv.visitFrame(Opcodes.F_APPEND, 1, new Object[]{name}, 0, null);
                } else {
                    mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
                }
                mv.visitVarInsn(ALOAD, 4);
                if (!methodInfo[1].startsWith("()")) {
                    MethodUtils.transfer(mv,methodInfo[1],3);
                }
                mv.visitMethodInsn(INVOKEVIRTUAL, name, methodInfo[0] + "$proxy", methodInfo[1], false);
                if (methodInfo[1].endsWith("V")) {
                    mv.visitInsn(ACONST_NULL);
                    mv.visitInsn(ARETURN);
                } else {
                    MethodUtils.returnType(mv, methodInfo[1]);
                    mv.visitInsn(ARETURN);
                }

            }
            mv.visitLabel(defaultLabel);
            mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
            mv.visitInsn(ACONST_NULL);
            mv.visitInsn(ARETURN);
            mv.visitMaxs(4, 5);
            mv.visitEnd();
        }
        FileUtils.saveFile("F:\\projects\\ASMDemo\\target\\classes\\com\\name.class", cw.toByteArray());
        return cw.toByteArray();
    }


    public static void main(String[] args) throws IOException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
        Class<JavaProxy> beanClass = JavaProxy.class;
        String proxyClass = beanClass.getName().replaceAll("\\.", "/") + "$proxy" + (UUID.randomUUID().toString().substring(0, 5));
        MethodFastClassBuilder builder = new MethodFastClassBuilder(beanClass, proxyClass);
        byte[] bytes = builder.create();
        Class<?> aClass = builder.defineClass((proxyClass+"$proxy$MethodFastClass").replaceAll("/", "\\."), bytes, 0, bytes.length);
        Method index = aClass.getMethod("getIndex", String.class);

        Object invoke = index.invoke(aClass.newInstance(), "test1()V");
        System.out.println(invoke);

    }
}
