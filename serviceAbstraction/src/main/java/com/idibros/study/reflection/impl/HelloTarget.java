package com.idibros.study.reflection.impl;

import com.idibros.study.reflection.Hello;

/**
 * Created by dongba on 2017-10-26.
 */
public class HelloTarget implements Hello {
    @Override
    public String sayHello(String name) {
        return "Hello " + name;
    }

    @Override
    public String sayHi(String name) {
        return "Hi " + name;
    }

    @Override
    public String sayThankYou(String name) {
        return "Thank you " + name;
    }
}
