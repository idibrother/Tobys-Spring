package com.idibros.study.factory;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

/**
 * Created by dongba on 2017-11-06.
 */
public class UpperCaseAdvice implements MethodInterceptor {

    /**
     * 여기서 MethodInvocation 매개변수로 타겟 오브젝트를 참조하고 있다고 하고,
     * proceed를 호출 하면 타겟의 메소드를 내부적으로 실행해준다고 한다.
     * @param invocation
     * @return
     * @throws Throwable
     */
    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        String ret = (String) invocation.proceed();
        return ret.toUpperCase();
    }
}
