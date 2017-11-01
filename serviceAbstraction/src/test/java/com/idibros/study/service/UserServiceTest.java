package com.idibros.study.service;

import com.idibros.study.dao.DaoFactory;
import com.idibros.study.dao.UserDao;
import com.idibros.study.dto.Level;
import com.idibros.study.dto.User;
import com.idibros.study.factory.TxProxyFactoryBean;
import com.idibros.study.reflection.proxy.TransactionHandler;
import com.idibros.study.service.impl.UserServiceImpl;
import com.idibros.study.service.impl.UserServiceTx;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
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

        TestUserService testUserService = new TestUserService("foo21");
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
    public void upgradeLevels_목_객체_활용() throws SQLException, ClassNotFoundException {
        /**
         * userDao 목 객체를 만들어서 userService에 설정
         */
        UserDao userDaoMock = mock(UserDao.class);
        ((UserServiceImpl)userServiceImpl).setUserDao(userDaoMock);

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
        TestUserService testUserService = new TestUserService("foo21");
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

        TestUserService testUserService = new TestUserService("foo21");
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
        } catch (TestUserService.TestUserServiceException e) {

        }

        checkLevelUpgraded(user2, false);
    }

    /**
     * 무엇에 대한 체크인지 파악이 어렵기 때문에 리팩토링한다.
     */
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