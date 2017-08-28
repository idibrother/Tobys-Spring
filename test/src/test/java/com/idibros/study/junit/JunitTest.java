package com.idibros.study.junit;

import com.idibros.study.TestDaoContext;
import javafx.application.Application;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Created by dongba on 2017-08-28.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestDaoContext.class)
public class JunitTest {

    private static JunitTest testObject;

    private static Set<JunitTest> testObjects = new HashSet<JunitTest>();

    @Autowired
    private ApplicationContext context;

    private static ApplicationContext contextObject;

    @Test
    public void test1() {
        // 1. Junit이 매 유닛테스트마다 인스턴스를 생성하는 것을 확인한다.
        // 2. 아래 버전으 테스트는 test1과 test3번이 다른 인스턴스라는 것을 보장 할 수 없다. 그래서...
//        assertThat(this, is(not(sameInstance(testObject))));
//        testObject = this;
        assertThat(testObjects, not(hasItem(this)));
        testObjects.add(this);

        // 3. 스프링 컨텍스트가 유닛 테스트 갯수에 상관 없이 하나만 만들어지는 테스트를 작성해보자.
        assertThat(contextObject == null || contextObject == this.context, is(true));
        contextObject = this.context;
    }

    @Test
    public void test2() {
//        assertThat(this, is(not(sameInstance(testObject))));
//        testObject = this;
        assertThat(testObjects, not(hasItem(this)));
        testObjects.add(this);

        assertThat(contextObject == null || contextObject == this.context, is(true));
        contextObject = this.context;
    }

    @Test
    public void test3() {
//        assertThat(this, is(not(sameInstance(testObject))));
//        testObject = this;
        assertThat(testObjects, not(hasItem(this)));
        testObjects.add(this);

        assertThat(contextObject == null || contextObject == this.context, is(true));
        contextObject = this.context;
    }

}
