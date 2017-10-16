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

        userDao.add(this.user1);
        userDao.add(this.user2);
        userDao.add(this.user3);
        userDao.add(this.user4);
        userDao.add(this.user5);
    }

    @Test
    public void bean() {
        assertThat(this.userService, is(notNullValue()));
    }

    @Test
    public void upgradeLevels() throws SQLException, ClassNotFoundException {
        /**
         * Dao는 순수가게 CRUD 기능만 수행하고,
         * service layer에서 비즈니스 로직을 담당한다.
         */
        userService.upgradeLevels();
        checkLevel(userDao.get(user1.getId()), Level.BASIC);
        checkLevel(userDao.get(user2.getId()), Level.SILVER);
        checkLevel(userDao.get(user3.getId()), Level.SILVER);
        checkLevel(userDao.get(user4.getId()), Level.GOLD);
        checkLevel(userDao.get(user5.getId()), Level.GOLD);
    }

    private void checkLevel(User user, Level expectedLevel) throws SQLException, ClassNotFoundException {
        User result = userDao.get(user.getId());
        assertThat(user.getLevel(), is(result.getLevel()));
    }

}