package com.idibros.study.junit;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Created by dongba on 2017-08-28.
 */
public class JunitTest {

    private static JunitTest testObject;

    @Test
    public void test1() {
        // Junit이 매 유닛테스트마다 인스턴스를 생성하는 것을 확인한다.
        assertThat(this, is(not(sameInstance(testObject))));
        testObject = this;
    }

    @Test
    public void test2() {
        assertThat(this, is(not(sameInstance(testObject))));
        testObject = this;
    }

    @Test
    public void test3() {
        assertThat(this, is(not(sameInstance(testObject))));
        testObject = this;
    }

}
