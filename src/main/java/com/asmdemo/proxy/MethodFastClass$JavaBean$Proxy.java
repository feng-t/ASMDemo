package com.asmdemo.proxy;


public class MethodFastClass$JavaBean$Proxy implements MethodFastClass {
    @Override
    public int getIndex(Signature signature) {
        String s = signature.toString();
        switch (s){
            case "test1()V":
                return 0;
            case "test2()I":
                return 1;
            case "test3(II)I":
                return 2;
        }
        return -1;
    }

    @Override
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
}
