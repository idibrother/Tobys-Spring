package com.idibros.study.reflection.proxy;

import com.idibros.study.factory.UpperCaseAdvice;
import com.idibros.study.reflection.Hello;
import com.idibros.study.reflection.impl.HelloTarget;
import com.idibros.study.reflection.impl.HelloToby;
import com.idibros.study.reflection.impl.HelloWorld;
import org.junit.Test;
import org.springframework.aop.ClassFilter;
import org.springframework.aop.framework.ProxyFactoryBean;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.aop.support.NameMatchMethodPointcut;

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

    /**
     * 포인트컷의 기능에는 메소드 확인과 클래스 확인이 있다.
     * 그 중에 이번에는 클래스 이름을 확인하는 기능을 테스트를 통해 알아본다.
     */
    @Test
    public void classNamePointcutAdvisor () {
        /**
         * 클래스명으로 필터링하는 포인트컷을 준비한다.
         */
        NameMatchMethodPointcut pointcut = new NameMatchMethodPointcut() {

            public ClassFilter getClassFilter () {
                return new ClassFilter() {
                    @Override
                    public boolean matches(Class<?> clazz) {
                        return clazz.getSimpleName().startsWith("HelloT");
                    }
                };
            }
        };

        /**
         * 대상 메소드를 설정한다.
         */
        pointcut.setMappedName("sayH*");

        ProxyFactoryBean proxyFactoryBean = new ProxyFactoryBean();

        /**
         * 프록시 팩토리 빈에 타겟을 설정하고 어드바이저(포인트컷 + 어드바이스)를 추가한다.
         */
        HelloTarget target = new HelloTarget();
        proxyFactoryBean.setTarget(target);
        DefaultPointcutAdvisor advisor = new DefaultPointcutAdvisor(pointcut, new UpperCaseAdvice());
        proxyFactoryBean.addAdvisor(advisor);

        Hello proxiedHello = (Hello) proxyFactoryBean.getObject();

        /**
         * 타겟의 클래스가 HelloT*에 속하고, 메소드가 sayH*에 포함되기 때문에 어드바이스가 적용된 결과를 반환 할 것이다.
         */
        String result = proxiedHello.sayHi("toby");
        assertThat(result, is("HI TOBY"));

        /**
         * 하나의 클래스에 대해서만 테스트를 했기 때문에 기대한대로 동작하는지 확인했다고 보기는 어렵다.
         * 그래서 추가로 클래스 이름(HelloT*가 아닌) 타겟 클래스를 정의해서 테스트해본다.
         */

        ProxyFactoryBean proxyFactoryBean1 = new ProxyFactoryBean();
        HelloWorld target1 = new HelloWorld();
        proxyFactoryBean1.setTarget(target1);
        proxyFactoryBean1.addAdvisor(advisor);
        /**
         * 같은 어드바이저를 적용했지만 클래스명으로 필터링 될 것이므로
         * 결과를 확인하면 대문자로 변경하는 부가기능이 빠져있는 것을 볼 수 있을 것이다.
         */
        Hello proxiedHello1 = (Hello) proxyFactoryBean1.getObject();

        String result1 = proxiedHello1.sayHi("toby");
        assertThat(result1, is("hi toby"));

        /**
         * 좀 더 확실한 테스트를 위해 HelloT*를 포함하는 클래스를 하나 더 정의해서 확인해보자.
         */
        ProxyFactoryBean proxyFactoryBean2 = new ProxyFactoryBean();
        HelloToby target2 = new HelloToby();
        proxyFactoryBean2.setTarget(target2);
        /**
         * 역시 동일한 어드바이저를 설정한다.
         */
        proxyFactoryBean2.addAdvisor(advisor);

        Hello proxiedFactoryBean2 = (Hello) proxyFactoryBean2.getObject();
        String result2 = proxiedFactoryBean2.sayHi("toby");
        /**
         * HelloToby가 HelloT*에 포함되기 때문에 대문자로 변환된 결과를 볼 것이다.
         */
        assertThat(result2, is("HI TOBY"));
    }

}