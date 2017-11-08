package com.idibros.study.service;

import com.idibros.study.dao.DaoFactory;
import com.idibros.study.dao.UserDao;
import com.idibros.study.dto.Level;
import com.idibros.study.dto.User;
import com.idibros.study.factory.TxProxyFactoryBean;
import com.idibros.study.factory.UpperCaseAdvice;
import com.idibros.study.reflection.Hello;
import com.idibros.study.reflection.impl.HelloTarget;
import com.idibros.study.reflection.proxy.TransactionHandler;
import com.idibros.study.reflection.proxy.UppercaseHandler;
import com.idibros.study.service.impl.TestUserServiceImpl;
import com.idibros.study.service.impl.UserServiceImpl;
import com.idibros.study.service.impl.UserServiceTx;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.springframework.aop.framework.ProxyFactoryBean;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.aop.support.NameMatchMethodPointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.lang.reflect.Proxy;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.util.MatcherAssertionErrors.assertThat;

/**
 * Created by dongba on 2017-10-16.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {DaoFactory.class})
public class UserServiceTest {

    @Autowired
    private UserService userServiceImpl;

    @Autowired
    private UserDao userDao;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private DataSourceTransactionManager transactionManager;

    @Autowired
    private ProxyFactoryBean proxyFactoryBean;

    @Autowired
    private UserService testUserServiceImpl;

    private User user1;

    private User user2;

    private User user3;

    private User user4;

    private User user5;

    @Before
    public void init() throws SQLException, ClassNotFoundException {
        userDao.deleteAll();

        /**
         * upgrade 조건에 대한 값을 상수로 설정한다.
         */
        this.user1 = new User("foo1", "bar1", "pw1", Level.BASIC, 49, 0);
        this.user2 = new User("foo11", "bar12", "pw12", Level.BASIC, UserServiceImpl.MIN_LOGCOUNT_FOR_SILVER, 0);
        this.user3 = new User("foo2", "bar2", "pw2", Level.SILVER, 60, 29);
        this.user4 = new User("foo21", "bar22", "pw22", Level.SILVER, 60, UserServiceImpl.MIN_RECOMMEND_FOR_GOLD);
        this.user5 = new User("foo3", "bar3", "pw3", Level.GOLD, 100, 100);

    }

    @Test
    public void bean() {
        assertThat(this.userServiceImpl, is(notNullValue()));
    }

    @Test
    public void testAllOrNothing() throws SQLException, ClassNotFoundException {
        userDao.add(this.user1);
        userDao.add(this.user2);
        userDao.add(this.user3);
        userDao.add(this.user4);

        TestUserServiceImpl testUserService = new TestUserServiceImpl("foo21");
        testUserService.setUserDao(userDao);

        /**
         * 트랜젝션 기능이 없는 UserService 구현체에게 트랜젝션 이외의 작업을 위임하고
         * 트랜젝션 관리 기능만 담당한다.
         * 이번 테스트에서는 일부 유저일 경우 일부러 예외를 발생시켜서 롤백처리를 하기 때문에
         * 이를 구현한 테스트용 UserService 구현체를 사용한다.
         */
        UserServiceTx userServiceTx = new UserServiceTx();
        userServiceTx.setUserService(testUserService);
        userServiceTx.setTransactionManager(transactionManager);

        userServiceTx.upgradeLevels();

        /**
         * user4에 대해서 예외를 고의를 발생시켰으므로 user2번이 원복되는 것을 원한다.
         * 하지만...
         * 하나의 트랜젝션은 모두 정상 완료 하거나 그렇지 않거나여야 한다.
         * 하지만 user4에서 예외가 발생해도 user2는 업그레이드가 되어 있다.
         * 트랜젝션이 깨진 것이다.
         * 원하는 결과는 트랜젝션 중간에 실패가 있을 경우 모든 유저의 업그레이드 작업을 원상복귀하는 것이다.
         */
        checkLevelUpgraded(user2, false);
    }

    @Test
    public void upgradeLevels() throws SQLException, ClassNotFoundException {
        userDao.add(this.user1);
        userDao.add(this.user2);
        userDao.add(this.user3);
        userDao.add(this.user4);

        userServiceImpl.upgradeLevels();

        checkLevelUpgraded(user2, true);
        checkLevelUpgraded(user4, true);
    }

    @Test
    public void add() throws SQLException, ClassNotFoundException {
        user1.setLevel(null);

        /**
         * 유저 레벨 정보가 있는 경우
         */
        userServiceImpl.add(user5);
        assertThat(userDao.get(user5.getId()).getLevel(), is(Level.GOLD));

        /**
         * 유저 레벨 정보가 없는 경우
         */
        userServiceImpl.add(user1);
        assertThat(userDao.get(user1.getId()).getLevel(), is(Level.BASIC));
    }

    @Test
    @Ignore
    public void upgradeLevels_목_객체_활용() throws SQLException, ClassNotFoundException {
        /**
         * userDao 목 객체를 만들어서 userService에 설정
         */
        UserDao userDaoMock = mock(UserDao.class);
        ((UserServiceImpl) userServiceImpl).setUserDao(userDaoMock);

        ArrayList<User> users = new ArrayList<>();
        users.add(user1);
        users.add(user2);
        users.add(user3);
        users.add(user4);

        /**
         * 모든 유저를 조회 했을 때 디비와 연동하지 않아도 유저 목록을 가져오도록 아래와 같이 설정
         */
        when(userDaoMock.getAll()).thenReturn(users);

        /**
         * upgradeLevels를 호출 하면 내부적으로 userDaoMock으로부터 설정한 유저 목록을 조회함(DB와 독립적으로 동작)
         * userDaoMock의 update를 호출하면 아무 동작도 하지 않도록 설정
         */
        doNothing().when(userDaoMock).update(Matchers.any(User.class));
        userServiceImpl.upgradeLevels();

        /**
         * 결과 확인을 위해 userDaoMock으로부터 모든 유저를 가져와서 검증
         */
        List<User> resultUsers = userDaoMock.getAll();
        /**
         * checkLevelUpgraded는 실제 DB와 연동하는 것을 기준으로 작성했기 때문에 해당 케이스에서는 활용 할 수 없다.
         */
//        checkLevelUpgraded(resultUsers.get(0), false);
//        checkLevelUpgraded(resultUsers.get(1), true);
//        checkLevelUpgraded(resultUsers.get(2), false);
//        checkLevelUpgraded(resultUsers.get(3), true);
        assertThat(resultUsers.get(0).getLevel(), is(Level.BASIC));
        assertThat(resultUsers.get(1).getLevel(), is(Level.SILVER));
        assertThat(resultUsers.get(2).getLevel(), is(Level.SILVER));
        assertThat(resultUsers.get(3).getLevel(), is(Level.GOLD));

        /**
         * 선택이긴 하지만 테스트용 컨텍스트를 구성해서 임베디드 디비를 활용하여 실제 디비가 아니어도 테스트가 가능하다.
         * 성능차이는 있을 수도 있지만 임베디드 디비이기 때문에 크게 차이가 날것같진 않다.
         * 확인해봤는데 임베디드 디비를 사용한 테스트가 더 빨랐다.
         */

        /**
         * 다른 유닛테스트에 영향을 주지 않기 위해 userDao를 원복시켜야 한다.
         */
        ((UserServiceImpl) userServiceImpl).setUserDao(userDao);
    }

    @Test
    public void transactionTestWithDynamicProxy () throws SQLException, ClassNotFoundException {
        userDao.add(this.user1);
        userDao.add(this.user2);
        userDao.add(this.user3);
        userDao.add(this.user4);

        /**
         * 앞에서의 예제와 동일하게 특정 유저에 대한 upgradeLevel 실행 할 때 예외를 발생시키는 테스트용 UserService를 사용한다.
         */
        TestUserServiceImpl testUserService = new TestUserServiceImpl("foo21");
        testUserService.setUserDao(userDao);

        TransactionHandler transactionHendler = new TransactionHandler();
        transactionHendler.setTarget(testUserService);
        transactionHendler.setPattern("upgrade");
        transactionHendler.setTransactionManager(transactionManager);

        UserService userService = (UserService) Proxy.newProxyInstance(getClass().getClassLoader(), new Class[] {UserService.class}, transactionHendler);

        try {
            userService.upgradeLevels();
        } catch (Exception e) {
        }

        /**
         * user4에 대해서 예외를 고의를 발생시켰으므로 user2번이 원복되는 것을 원한다.
         * 하지만...
         * 하나의 트랜젝션은 모두 정상 완료 하거나 그렇지 않거나여야 한다.
         * 하지만 user4에서 예외가 발생해도 user2는 업그레이드가 되어 있다.
         * 트랜젝션이 깨진 것이다.
         * 원하는 결과는 트랜젝션 중간에 실패가 있을 경우 모든 유저의 업그레이드 작업을 원상복귀하는 것이다.
         */
        checkLevelUpgraded(user2, false);

    }

    @Test
    public void upgradeAllOrNothing () throws Exception {
        userDao.add(this.user1);
        userDao.add(this.user2);
        userDao.add(this.user3);
        userDao.add(this.user4);

        TestUserServiceImpl testUserService = new TestUserServiceImpl("foo21");
        testUserService.setUserDao(userDao);

        TxProxyFactoryBean txProxyFactoryBean = (TxProxyFactoryBean) applicationContext.getBean("&userService");
        /**
         * 팩토리 빈을 활용해서 매번 트랜젝션 기능을 필요로 하는 클래스를 추가할 필요가 없어졌다?
         * txProxyFactoryBean도 Bean이기 때문에 target을 하나만 지정할 수 있는데...
         */
        txProxyFactoryBean.setTarget(testUserService);
        UserService txUserService = (UserService) txProxyFactoryBean.getObject();

        try {
            txUserService.upgradeLevels();
        } catch (TestUserServiceImpl.TestUserServiceException e) {

        }

        checkLevelUpgraded(user2, false);
    }

    @Test
    public void simpleProxy () {
        /**
         * JDK의 다이내믹 프록시를 사용해서 프록시를 생성
         * 이전 예제에서는 다이내믹 프록시를 빈으로 등록 후 타겟을 지정해서 재사용했고,
         * 지금은 스프링의 ProxyFactoryBean 기능을 확인하기 위해 기본적인 형태의 다이내믹 프록시를 사용하고 대조하는 것 같다.
         */
        UppercaseHandler uppercaseHandler = new UppercaseHandler();
        HelloTarget target = new HelloTarget();
        String name = "foo";
        String result1 = target.sayHello(name);
        assertThat(result1, is("Hello foo"));

        uppercaseHandler.setTarget(target);
        Hello proxy = (Hello) Proxy.newProxyInstance(getClass().getClassLoader(),
                new Class[]{Hello.class},
                uppercaseHandler);
        String result2 = proxy.sayHello(name);
        assertThat(result2, is("HELLO FOO"));
    }

    @Test
    public void proxyFactoryBean () {
        /**
         * ProxyFactoryBean은 타깃을 지정하고, 부가기능을 별도로 추가 할 수 있다.
         */
        ProxyFactoryBean pfBean = new ProxyFactoryBean();
        String name = "foo";
        HelloTarget target = new HelloTarget();
        String result1 = target.sayHello(name);
        assertThat(result1, is("Hello foo"));

        pfBean.setTarget(target);
        /**
         * 여기서 처음으로 어드바이스라는 용어가 나왔다.
         * 어드바이스는 타깃이 필요 없는 순수 부가기능이라고 한다.
         */
        pfBean.addAdvice(new UpperCaseAdvice());

        Hello proxiedHello = (Hello) pfBean.getObject();
        String result2 = proxiedHello.sayHello(name);
        assertThat(result2, is("HELLO FOO"));

        /**
         * 여러개도 가능하다고 한다.
         */
    }

    @Test
    public void pointcutAdvisor () {
        /**
         * 대상 메소드를 설정하는 것을 pointcut에 위임하는 구조를 활용한다.
         * 여기서 pointcut이라는 것의 정의는 어드바이스를 적용할 대상 메소드를 설정하는 것이라고 한다.
         */
        ProxyFactoryBean proxyFactoryBean = new ProxyFactoryBean();
        HelloTarget target = new HelloTarget();
        proxyFactoryBean.setTarget(target);

        NameMatchMethodPointcut pointcut = new NameMatchMethodPointcut();
        pointcut.setMappedName("sayH*");

        UpperCaseAdvice advice = new UpperCaseAdvice();
        /**
         * Advisor는 포인트컷과 어드바이스를 조합해주는 것 같다.
         */
        proxyFactoryBean.addAdvisor(new DefaultPointcutAdvisor(pointcut, advice));

        Hello proxiedHello = (Hello) proxyFactoryBean.getObject();

        String name = "foo";
        String result1 = "Thank you foo";
        /**
         * 포인트컷에 메소드 이름을 sayH*로 설정했기 때문에 Thank you는 적용 제외 대상이다.
         */
        assertThat(proxiedHello.sayThankYou(name), is(result1));

        /**
         * 나머지 sayHello와 sayHi는 포인트컷에 포함되므로 어드바이스가 적용된 결과를 리턴할 것이다.
         */
        String result2 = "HELLO FOO";
        assertThat(proxiedHello.sayHello(name), is(result2));
        String result3 = "HI FOO";
        assertThat(proxiedHello.sayHi(name), is(result3));
    }

    @Test
    @DirtiesContext
    public void applyProxyFactoryBeanToUserService () throws SQLException, ClassNotFoundException {
        userDao.add(this.user1);
        userDao.add(this.user2);
        userDao.add(this.user3);
        userDao.add(this.user4);

        TestUserServiceImpl testUserService = new TestUserServiceImpl("foo21");
        testUserService.setUserDao(userDao);
        /**
         * proxyFactoryBean을 사용하면 target이 구현한 모든 인터페이스를 대상으로 부가기능 적용 설정이 가능하다.
         */
        proxyFactoryBean.setTarget(testUserService);

        UserService proxiedUserService = (UserService) proxyFactoryBean.getObject();
        try {
            proxiedUserService.upgradeLevels();
        } catch (TestUserServiceImpl.TestUserServiceException e) {

        }

        checkLevelUpgraded(user2, false);
    }

    @Test
    @DirtiesContext
    public void applyAutoProxyFactoryBeanToUserService () throws SQLException, ClassNotFoundException {
        userDao.add(this.user1);
        userDao.add(this.user2);
        userDao.add(this.user3);
        userDao.add(this.user4);

        /**
         * 자동 프록시 생성기에서 정의한 클래스 필터를 적용한 포인트컷의 정의 내용중 클래스명이 *ServiceImpl이기 때문에
         * TestUserService으로는 테스트가 불가하기 때문에 변경한다.
         */
        try {
            testUserServiceImpl.upgradeLevels();
        } catch (TestUserServiceImpl.TestUserServiceException e) {

        }

        checkLevelUpgraded(user2, false);
    }

    //    private void checkLevel(User user, Level expectedLevel) throws SQLException, ClassNotFoundException {
//        User result = userDao.get(user.getId());
//        assertThat(expectedLevel, is(result.getLevel()));
//    }
    private void checkLevelUpgraded(User user, boolean upgraded) throws SQLException, ClassNotFoundException {
        User userUpdate = userDao.get(user.getId());
        Level userUpdateLevel = userUpdate.getLevel();
        Level userLevel = user.getLevel();
        if (upgraded) {
            assertThat(userUpdateLevel, is(userLevel.nextLevel()));
        } else {
            assertThat(userUpdateLevel, is(userLevel));
        }
    }

}