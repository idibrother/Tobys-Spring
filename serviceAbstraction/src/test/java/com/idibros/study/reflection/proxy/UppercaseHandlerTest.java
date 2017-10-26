package com.idibros.study.reflection.proxy;

import com.idibros.study.reflection.Hello;
import com.idibros.study.reflection.impl.HelloTarget;
import org.junit.Test;

import java.lang.reflect.Proxy;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

/**
 * Created by dongba on 2017-10-26.
 */
public class UppercaseHandlerTest {

    @Test
    public void createProxy () {
        Hello hello = new HelloTarget();
        UppercaseHandler uppercaseHandler = new UppercaseHandler();
        uppercaseHandler.setTarget(hello);
        /**
         * 아래와 같이 Proxy 객체의 newInstance 함수를 써서 다이내믹 프록시를 생성 할 수 있다.
         */
        Hello proxiedHello = (Hello) Proxy.newProxyInstance(getClass().getClassLoader() //다이내믹 프록시가 정의되는 클래스 로더를 지정
                , new Class[]{Hello.class}  // 구현할 인터페이스
                , uppercaseHandler);    // 부가기능과 위임 코드를 담은 InvocationHandler

        String name = "Toby";
        assertThat(proxiedHello.sayHello(name), is("HELLO TOBY"));
        assertThat(proxiedHello.sayHi(name), is("HI TOBY"));
        assertThat(proxiedHello.sayThankYou(name), is("THANK YOU TOBY"));
    }

}