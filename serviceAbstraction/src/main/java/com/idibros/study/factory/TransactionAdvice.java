package com.idibros.study.factory;

import lombok.Setter;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.interceptor.DefaultTransactionAttribute;

/**
 * Created by dongba on 2017-11-06.
 */
public class TransactionAdvice implements MethodInterceptor {

    @Setter
    private PlatformTransactionManager transactionManager;

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        TransactionStatus status = transactionManager.getTransaction(new DefaultTransactionAttribute());

        try {
            Object ret = invocation.proceed();
            transactionManager.commit(status);
            return ret;
        } catch (RuntimeException e) {
            transactionManager.rollback(status);
            throw e;
        }
    }

}
