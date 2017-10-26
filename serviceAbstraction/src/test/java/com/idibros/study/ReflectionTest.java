package com.idibros.study;

import org.junit.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Created by dongba on 2017-10-26.
 */
public class ReflectionTest {

    @Test
    public void invokeMethod () throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        String name = "Spring";

        /**
         * 일반적으로 사용하는 문자열 길이 함수
         */
        assertThat(name.length(), is(6));

        /**
         * 자바 리플렉션을 사용한 문자열 길이 함수 호출
         */
        Method lengthMethod = String.class.getMethod("length");
        Integer length = (Integer) lengthMethod.invoke(name);
        assertThat(length, is(6));

        /**
         * 일반적으로 사용하는 문자 추출 함수
         */
        assertThat(name.charAt(0), is('S'));

        /**
         * 자바 리플렉션을 사용한 문자 추출 함수 호출
         */
        Method charAtMethod = String.class.getMethod("charAt", int.class);
        Character character = (Character) charAtMethod.invoke(name, 0);
        assertThat(character, is('S'));
    }

}
