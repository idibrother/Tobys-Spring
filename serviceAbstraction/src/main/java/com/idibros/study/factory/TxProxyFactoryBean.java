package com.idibros.study.factory;

import com.idibros.study.reflection.proxy.TransactionHandler;
import lombok.Setter;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import java.lang.reflect.Proxy;

/**
 * Created by dongba on 2017-11-01.
 * 다이내믹 프록시를 빈으로 등록하려면 팩토리 빈을 사용해야 한다고 한다.
 */
public class TxProxyFactoryBean implements FactoryBean<Object> {

    /**
     * 트랜젝션과 패턴을 사용해서 부가 기능을 적용 할 대상 객체
     */
    @Setter
    private Object target;

    /**
     * InvocationHandler에서 사용할 트랜젝션 매니저
     */
    @Setter
    private PlatformTransactionManager transactionManager;

    /**
     * InvocationHandler에서 사용할 패턴 변수
     */
    @Setter
    private String pattern;

    /**
     * 다이내믹 프록시에서 사용 할 인터페이스
     */
    @Setter
    private Class<?> serviceInterface;

    @Override
    public Object getObject() throws Exception {
        TransactionHandler txHandler = new TransactionHandler();
        txHandler.setTarget(target);
        txHandler.setPattern(pattern);
        txHandler.setTransactionManager(transactionManager);
        return Proxy.newProxyInstance(getClass().getClassLoader(), new Class[] {serviceInterface}, txHandler);
    }

    @Override
    public Class<?> getObjectType() {
        return serviceInterface;
    }

    @Override
    public boolean isSingleton() {
        return false;
    }

}
