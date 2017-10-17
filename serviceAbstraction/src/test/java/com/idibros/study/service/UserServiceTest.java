package com.idibros.study.service;

import com.idibros.study.dao.DaoFactory;
import com.idibros.study.dao.UserDao;
import com.idibros.study.dto.Level;
import com.idibros.study.dto.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.sql.SQLException;

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

    private User user1;

    private User user2;

    private User user3;

    private User user4;

    private User user5;

    @Before
    public void init() throws SQLException, ClassNotFoundException {
        userDao.deleteAll();

        this.user1 = new User("foo1", "bar1", "pw1", Level.BASIC, 49, 0);
        this.user2 = new User("foo11", "bar12", "pw12", Level.BASIC, 50, 0);
        this.user3 = new User("foo2", "bar2", "pw2", Level.SILVER, 60, 29);
        this.user4 = new User("foo21", "bar22", "pw22", Level.SILVER, 60, 30);
        this.user5 = new User("foo3", "bar3", "pw3", Level.GOLD, 100, 100);

    }

    @Test
    public void bean() {
        assertThat(this.userService, is(notNullValue()));
    }

    @Test
    public void upgradeLevels() throws SQLException, ClassNotFoundException {
        userDao.add(this.user1);
        userDao.add(this.user2);
        userDao.add(this.user3);
        userDao.add(this.user4);
        userDao.add(this.user5);

        userService.upgradeLevels();
        checkLevel(userDao.get(user1.getId()), Level.BASIC);
        checkLevel(userDao.get(user2.getId()), Level.SILVER);
        checkLevel(userDao.get(user3.getId()), Level.SILVER);
        checkLevel(userDao.get(user4.getId()), Level.GOLD);
        checkLevel(userDao.get(user5.getId()), Level.GOLD);
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

    private void checkLevel(User user, Level expectedLevel) throws SQLException, ClassNotFoundException {
        User result = userDao.get(user.getId());
        assertThat(user.getLevel(), is(result.getLevel()));
    }

}