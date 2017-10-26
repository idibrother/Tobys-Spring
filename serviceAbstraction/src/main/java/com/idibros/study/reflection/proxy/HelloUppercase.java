package com.idibros.study.reflection.proxy;

import com.idibros.study.reflection.Hello;
import lombok.Setter;

/**
 * Created by dongba on 2017-10-26.
 */
public class HelloUppercase implements Hello {

    /**
     * 일반적인 프록시 패턴을 활용해서 부가기능을 구현
     */

    @Setter
    private Hello hello;

    @Override
    public String sayHello(String name) {
        return hello.sayHello(name).toUpperCase();
    }

    @Override
    public String sayHi(String name) {
        return hello.sayHi(name).toUpperCase();
    }

    @Override
    public String sayThankYou(String name) {
        return hello.sayThankYou(name).toUpperCase();
    }
}
