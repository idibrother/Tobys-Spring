package com.idibros.study.reflection.proxy;

import com.idibros.study.reflection.Hello;
import lombok.Setter;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * Created by dongba on 2017-10-26.
 */
public class UppercaseHandler implements InvocationHandler {

    @Setter
    private Hello target;

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object ret = method.invoke(target, args);
        /**
         * 자바 리플렉션 패키지의 Method 오브젝트를 사용하므로 파라미터 갯수, 리턴 값, 메소드 이름 등을 통해 부가기능을 추가 할 수 있다.
         */
        if (ret instanceof String) {
            return ((String) ret).toUpperCase();
        }
        return ret;
    }

}
