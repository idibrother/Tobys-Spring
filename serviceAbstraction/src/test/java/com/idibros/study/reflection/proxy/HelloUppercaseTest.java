package com.idibros.study.reflection.proxy;

import com.idibros.study.reflection.impl.HelloTarget;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

/**
 * Created by dongba on 2017-10-26.
 */
public class HelloUppercaseTest {

    @Test
    public void helloUpperCase () {
        HelloUppercase helloUppercase = new HelloUppercase();
        HelloTarget helloTarget = new HelloTarget();

        helloUppercase.setHello(helloTarget);

        assertThat(helloUppercase.sayHello("Toby"), is("HELLO TOBY"));
        assertThat(helloUppercase.sayHi("Toby"), is("HI TOBY"));
        assertThat(helloUppercase.sayThankYou("Toby"), is("THANK YOU TOBY"));
    }

}