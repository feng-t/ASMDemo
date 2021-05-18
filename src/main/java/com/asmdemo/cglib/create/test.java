package com.asmdemo.cglib.create;

import com.asmdemo.cglib.proxy.JavaBean$Proxy;
import org.objectweb.asm.Type;

import java.util.Arrays;

public class test {

    public static void main(String[] args) {
        String s="(II)Z";
        Type type = Type.getType(s);
        System.out.println(Arrays.toString(type.getArgumentTypes()));
    }
    public int getIndex(String var1) {
        int var2 = var1.hashCode();
        switch(var2) {

            case -1222426664:
                return 1;
            case 806122219:
                return 0;
            case -1221503156:
                return 2;
            case -450002771:
                return 3;
            default:
                return 999;
        }
    }
    public Object invoke(int index, Object obj, Object[] parameter) {
        JavaBean$Proxy bean = (JavaBean$Proxy) obj;
        switch (index){
            case 0:
                bean.test1$proxy();
                return null;
            case 1:
                return bean.test2$proxy();
            case 2:
                return bean.test3$proxy((int)parameter[0],(int)parameter[1]);
        }
        return null;
    }
    public Object invoke1(int index, Object obj,Object o2, Object[] parameter) {
        test1((int)obj,(Integer) obj);
        test1((long)obj,(Long) obj);
        test1((char)obj,(Character) obj);
        test1((byte)obj,(Byte) obj);
        test1((byte)o2,(Byte) obj,9);
        test1((int)parameter[0],(Integer) parameter[1],(int) parameter[2],(boolean)parameter[9]);

        return null;
    }

    public void test1(int i,Integer k){}
    public void test1(int i,Integer k,int l,Boolean kd){}
    public void test1(long i,Long k){}
    public void test1(char i,Character k){}
    public void test1(byte i,Byte k){}
    public void test1(byte i,Byte k,int kd){}
}
