package com.idibros.study.dao;

import com.idibros.study.dto.User;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.sql.SQLException;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Created by dongba on 2017-08-21.
 */
public class UserDaoTest {

    private static UserDaoDeleteAll userDaoDeleteAll;

    private static UserDaoAdd userDaoAdd;

    private static UserDaoGet userDaoGet;

    @BeforeClass
    public static void init() throws ClassNotFoundException {
        ApplicationContext context = new AnnotationConfigApplicationContext(DaoFactory.class);
        userDaoDeleteAll = context.getBean("userDaoDeleteAll", UserDaoDeleteAll.class);
        userDaoAdd = context.getBean("userDaoAdd", UserDaoAdd.class);
        userDaoGet = context.getBean("userDaoGet", UserDaoGet.class);
    }

    // 유닛 테스트마다 상호 독립적으로 수행 할 수 있도록 각 테스트를 실행 할 때 마다 테이블을 제거하고 새로 만듦.
    @Before
    public void setUp() throws ClassNotFoundException, SQLException {
        userDaoDeleteAll.deleteAll();
    }

    @Test
    public void add() throws SQLException, ClassNotFoundException {
        User user = new User();
        user.setId("foo");
        user.setName("idibros");
        user.setPassword("password");

        userDaoAdd.add(user);
    }

    @Test
    public void get() throws SQLException, ClassNotFoundException {
        User user = new User();
        user.setId("foo");
        user.setName("idibros");
        user.setPassword("password");

        userDaoAdd.add(user);

        User result = userDaoGet.get(user.getId());
        assertThat(result.getId(), is(user.getId()));
        assertThat(result.getName(), is(user.getName()));
        assertThat(result.getPassword(), is(user.getPassword()));
    }

}