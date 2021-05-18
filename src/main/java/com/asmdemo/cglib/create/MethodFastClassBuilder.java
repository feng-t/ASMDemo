package com.asmdemo.cglib.create;

import com.asmdemo.cglib.proxy.JavaBean;
import com.asmdemo.cglib.proxy.MethodFastClass;
import com.asmdemo.utils.ClassUtils;
import com.asmdemo.utils.FileUtils;
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
        MethodVisitor mv;
        {
            mv = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
            mv.visitInsn(RETURN);
            mv.visitMaxs(1, 1);
            mv.visitEnd();
        }
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
            System.out.println("打印方法");
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
            Integer[] array = methodToIndex.keySet().toArray(new Integer[0]);
            Arrays.sort(array);
            for (int i = 0; i < labels.length; i++) {
                mv.visitLabel(labels[i]);
                String s = methodToIndex.get(array[i]);
                String[] methodInfo = s.split("\\|");
                if (i == 0) {
                    mv.visitFrame(Opcodes.F_APPEND, 1, new Object[]{this.proxyName}, 0, null);
                } else {
                    mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
                }
                mv.visitVarInsn(ALOAD, 4);
                if (!methodInfo[1].startsWith("()")) {
                    //强转参数
                    Type[] types = Type.getType(methodInfo[1]).getArgumentTypes();
                    String[] parameterTypes = ClassUtils.getParameterTypes(methodInfo[1]);
                    for (int index = 0; index < types.length; index++) {
                        String ow = parameterTypes[index].replaceAll("\\.", "/");
                        String ty = types[index].getClassName();
                        System.out.print(ow+"\t");
                        System.out.print(Arrays.toString(methodInfo) +"\t");
                        System.out.println(ty);
                        mv.visitVarInsn(ALOAD, 3);
                        if (index <= 5) {
                            mv.visitInsn(i + 3);
                        } else {
                            mv.visitIntInsn(BIPUSH, index);
                        }
                        mv.visitInsn(AALOAD);
                        //强转参数
                        mv.visitTypeInsn(CHECKCAST, ow);
                        if (!ty.contains(".")) {
                            mv.visitMethodInsn(INVOKEVIRTUAL, ow,  ty+ "Value","()"+methodInfo[1].split("\\)")[1], false);
                        }
                    }
                }
                mv.visitMethodInsn(INVOKEVIRTUAL, this.proxyName, methodInfo[0] + "$proxy", methodInfo[1], false);
                if (methodInfo[1].endsWith("V")) {
                    mv.visitInsn(ACONST_NULL);
                    mv.visitInsn(ARETURN);
                } else {
//                    mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
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
        FileUtils.saveFile("/Users/hu/IdeaProjects/ASMDemo/target/classes/name.class", cw.toByteArray());
        return cw.toByteArray();
    }


    public static void main(String[] args) throws IOException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
        Class<JavaBean> beanClass = JavaBean.class;
        String proxyClass = beanClass.getName().replaceAll("\\.", "/") + "$proxy" + (UUID.randomUUID().toString().substring(0, 5));
        MethodFastClassBuilder builder = new MethodFastClassBuilder(beanClass, proxyClass);
        byte[] bytes = builder.create();
        Class<?> aClass = builder.defineClass((proxyClass + "$proxy$MethodFastClass").replaceAll("/", "\\."), bytes, 0, bytes.length);
        Method index = aClass.getMethod("getIndex", String.class);

        Object invoke = index.invoke(aClass.newInstance(), "test1()V");
        System.out.println(invoke);

    }
}
