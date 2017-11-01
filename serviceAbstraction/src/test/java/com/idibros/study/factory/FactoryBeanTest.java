package com.idibros.study.factory;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * Created by dongba on 2017-10-31.
 * ContextConfiguration 어노테이션에 클래스나 파일을 지정하지 않으면 클래스명+-context.xml을 디폴트로 사용한다고 한다.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:/FactoryBeanTest-Context.xml")
public class FactoryBeanTest {

    @Autowired
    private ApplicationContext context;

    @Test
    public void getMessageFromFactoryBean () {
        Object message = context.getBean("message");
        assertThat(message, is(Message.class));
        assertThat(((Message)message).getText(), is("Factory Bean"));

        /**
         * 조회 할 빈 이름 앞에 &를 붙이면 팩토리빈 자체를 로드한다고 한다.
         */
        Object contextBean = context.getBean("&message");
        assertThat(contextBean, is(MessageFactoryBean.class));
    }
}
