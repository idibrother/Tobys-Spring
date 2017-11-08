package com.idibros.study.reflection.impl;

import com.idibros.study.reflection.Hello;

/**
 * Created by dongba on 2017-11-08.
 */
public class HelloToby implements Hello {

    @Override
    public String sayHello(String name) {
        return "hello " + name;
    }

    @Override
    public String sayHi(String name) {
        return "hi " + name;
    }

    @Override
    public String sayThankYou(String name) {
        return "thank you " + name;
    }

}
