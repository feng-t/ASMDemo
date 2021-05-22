package com.sproxy.test;

import com.sproxy.method.MethodFastClass;
import com.sproxy.method.Signature;

public class MethodFastClassImpl implements MethodFastClass {
    @Override
    public int getIndex(String signature) {
        String v1 = signature.toString();
        int i = v1.hashCode();
        switch (i){
            case -123123123:
                break;

        }
        return 0;
    }

    @Override
    public Object invoke(int index, Object obj, Object[] parameter) {
        JavaBean$$proxy var1 = (JavaBean$$proxy) obj;
        switch (index){
            case 0:
                var1.test();

                return null;
            case 1:
                var1.d((String) parameter[0]);
                return null;
            default:
                return null;
        }
    }
}
