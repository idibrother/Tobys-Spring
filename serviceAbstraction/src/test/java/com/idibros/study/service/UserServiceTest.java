package com.idibros.study.service;

import com.idibros.study.dao.DaoFactory;
import com.idibros.study.dao.DaoTestFactory;
import com.idibros.study.dao.UserDao;
import com.idibros.study.dto.Level;
import com.idibros.study.dto.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.sql.DataSource;
import java.sql.SQLException;

import static com.idibros.study.service.UserService.MIN_LOGCOUNT_FOR_SILVER;
import static com.idibros.study.service.UserService.MIN_RECOMMEND_FOR_GOLD;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

/**
 * Created by dongba on 2017-10-16.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {DaoFactory.class})
public class UserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserDao userDao;

    @Autowired
    private DataSource dataSource;

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
        this.user2 = new User("foo11", "bar12", "pw12", Level.BASIC, MIN_LOGCOUNT_FOR_SILVER, 0);
        this.user3 = new User("foo2", "bar2", "pw2", Level.SILVER, 60, 29);
        this.user4 = new User("foo21", "bar22", "pw22", Level.SILVER, 60, MIN_RECOMMEND_FOR_GOLD);
        this.user5 = new User("foo3", "bar3", "pw3", Level.GOLD, 100, 100);

    }

    @Test
    public void bean() {
        assertThat(this.userService, is(notNullValue()));
    }

    @Test
    public void testAllOrNothing() throws SQLException, ClassNotFoundException {
        userDao.add(this.user1);
        userDao.add(this.user2);
        userDao.add(this.user3);
        userDao.add(this.user4);

        TestUserService testUserService = new TestUserService("foo21");
        testUserService.setUserDao(this.userDao);
        testUserService.setDataSource(this.dataSource);

        testUserService.upgradeLevels();

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

        userService.upgradeLevels();

        checkLevelUpgraded(user2, true);
    }

    @Test
    public void add() throws SQLException, ClassNotFoundException {
        /**
         * user를 추가 할 때 레벨을 초기화 하는 기능을 추가하려고 한다.
         * 추가하려고 하는 user의 레벨이 있으면 그대로 사용하고,
         * 없으면 BASIC 레벨로 초기화하는 기능을 추가한다.
         * 이런 기능은 비즈니스 로직이므로 서비스레이어에 추가한다.
         */
        user1.setLevel(null);

        /**
         * 유저 레벨 정보가 있는 경우
         */
        userService.add(user5);
        assertThat(userDao.get(user5.getId()).getLevel(), is(Level.GOLD));

        /**
         * 유저 레벨 정보가 없는 경우
         */
        userService.add(user1);
        assertThat(userDao.get(user1.getId()).getLevel(), is(Level.BASIC));
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