package com.idibros.study.factory;

import lombok.Setter;
import org.springframework.beans.factory.FactoryBean;

/**
 * Created by dongba on 2017-10-31.
 */
public class MessageFactoryBean implements FactoryBean<Message> {

    @Setter
    private String text;

    /**
     * 스프링에서는 컨택스트로 생성한 객체와 팩토리빈에서 생성한 객체를 참조 할 수 있다.
     * Message 클래스는 생성자가 private이므로 컨택스트에서 사용하는 방법을 추천하지 않는다고 한다.(가능은 하다고 한다.)
     * 팩토리빈 구현 클래스를 정의하고 getObject 객체를 사용해서 객체를 생성할 수 있다고 한다.
     * @return
     * @throws Exception
     */
    @Override
    public Message getObject() throws Exception {
        return Message.newMessage(this.text);
    }

    @Override
    public Class<?> getObjectType() {
        return Message.class;
    }

    /**
     * getObject를 호출 할 때마다 객체 생성을 하는 것으로 설정하고 isSingleton 함수의 러턴값을 false로 지정한다.
     * @return
     */
    @Override
    public boolean isSingleton() {
        return false;
    }
}
