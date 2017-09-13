package com.idibros.study.dao;

import com.idibros.study.dto.User;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.dao.EmptyResultDataAccessException;

import java.sql.SQLException;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Created by dongba on 2017-08-21.
 */
public class UserDaoTest {

    private static UserDao userDao;

    @BeforeClass
    public static void init() throws ClassNotFoundException {
        ApplicationContext context = new AnnotationConfigApplicationContext(DaoFactory.class);
        userDao = context.getBean("userDao", UserDao.class);
    }

    // 유닛 테스트마다 상호 독립적으로 수행 할 수 있도록 각 테스트를 실행 할 때 마다 테이블을 제거하고 새로 만듦.
    @Before
    public void setUp() throws ClassNotFoundException, SQLException {
        userDao.deleteAll();
    }

    @Test
    public void add() throws SQLException, ClassNotFoundException {
        User user = new User();
        user.setId("foo");
        user.setName("idibros");
        user.setPassword("password");

        userDao.add(user);
    }

    @Test
    public void get() throws SQLException, ClassNotFoundException {
        User user = new User();
        user.setId("foo");
        user.setName("idibros");
        user.setPassword("password");

        userDao.add(user);

        User result = userDao.get(user.getId());
        assertThat(result.getId(), is(user.getId()));
        assertThat(result.getName(), is(user.getName()));
        assertThat(result.getPassword(), is(user.getPassword()));
    }

    @Test(expected = EmptyResultDataAccessException.class)
    public void get_id에_해당하는_조회결과가_없을경우() throws SQLException, ClassNotFoundException {
        /**
         * 1. 테스트 보완을 위해 id로 조회 결과가 없을 경우를 추가한다.
         */
        userDao.get("foo");
    }

    @Test
    public void getCount() throws SQLException, ClassNotFoundException {
        User user = new User();
        user.setId("foo");
        user.setName("idibros");
        user.setPassword("password");

        userDao.add(user);
        int count = userDao.getCount();
        assertThat(count, is(1));

        User user1 = new User();
        user1.setId("foo1");
        user1.setName("idibros1");
        user1.setPassword("password1");

        userDao.add(user1);
        count = userDao.getCount();
        assertThat(count, is(2));
    }

    @Test
    public void getAll() throws SQLException, ClassNotFoundException {
        User user1 = new User();
        user1.setId("foo1");
        user1.setName("idibros");
        user1.setPassword("password");
        userDao.add(user1);
        int count = userDao.getCount();
        assertThat(count, is(1));

        List<User> userList = userDao.getAll();
        User resultUser1 = userList.get(0);
        checksumUser(user1, resultUser1);

        User user2 = new User();
        user2.setId("foo2");
        user2.setName("idibros2");
        user2.setPassword("password2");
        userDao.add(user2);

        count = userDao.getCount();
        assertThat(count, is(2));

        userList.clear();
        userList.addAll(userDao.getAll());
        resultUser1 = userList.get(0);
        User resultUser2 = userList.get(1);
        checksumUser(user1, resultUser1);
        checksumUser(user2, resultUser2);

        User user3 = new User();
        user3.setId("foo3");
        user3.setName("idibros3");
        user3.setPassword("password3");
        userDao.add(user3);

        count = userDao.getCount();
        assertThat(count, is(3));

        userList.clear();
        userList.addAll(userDao.getAll());
        resultUser1 = userList.get(0);
        resultUser2 = userList.get(1);
        User resultUser3 = userList.get(2);
        checksumUser(user1, resultUser1);
        checksumUser(user2, resultUser2);
        checksumUser(user3, resultUser3);
    }

    @Test
    public void getAll_조회결과가_없는경우() {
        /**
         * 2. 유저 목록이 없는 경우도 테스트로 추가한다.
         */
        /**
         * 가능한 예상 결과를 모두 확인해서 내부코드가 변경되어도 잘 동작하는지 검증하는 습관을 가지는 것이 좋다.
         */
        List<User> result = userDao.getAll();
        assertThat(result.size(), is(0));
    }

    private void checksumUser(User user1, User resultUser1) {
        assertThat(user1.getId(), is(resultUser1.getId()));
        assertThat(user1.getName(), is(resultUser1.getName()));
        assertThat(user1.getPassword(), is(resultUser1.getPassword()));
    }

}