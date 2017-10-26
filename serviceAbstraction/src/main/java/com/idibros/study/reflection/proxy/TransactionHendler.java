package com.idibros.study.reflection.proxy;

import lombok.Setter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by dongba on 2017-10-26.
 */
public class TransactionHendler implements InvocationHandler {

    @Setter
    private Object target;

    @Setter
    private PlatformTransactionManager transactionManager;

    @Setter
    private String pattern;

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (method.getName().startsWith(pattern)) {
            return invokeTransaction(method, args);
        } else {
            return method.invoke(target, args);
        }
    }

    private Object invokeTransaction(Method method, Object[] args) throws Throwable {
        /**
         * InvocationHandler를 사용해서 추가 기능을 구현 할 수 있는데
         * 여기서는 transactionManager를 사용해서 메소드 실행 중 타겟에서 예외가 발생하면 롤백하는 기능을 구현한다.
         */
        TransactionStatus status = transactionManager.getTransaction(new DefaultTransactionDefinition());

        try {
            Object ret = method.invoke(target, args);
            transactionManager.commit(status);
            return ret;
        } catch (InvocationTargetException e) {
            transactionManager.rollback(status);
            throw e.getTargetException();
        }
    }

}
