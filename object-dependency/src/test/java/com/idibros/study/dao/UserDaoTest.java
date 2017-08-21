package com.idibros.study.dao;

import com.idibros.study.dto.User;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Created by dongba on 2017-08-21.
 */
public class UserDaoTest {

    private UserDao userDao;

    @BeforeClass
    public static void init() throws ClassNotFoundException {
        Class.forName("org.h2.Driver");
    }

    @Before
    public void setUp() throws ClassNotFoundException, SQLException {
        try(Connection conn = DriverManager.getConnection("jdbc:h2:~/object-dependency", "", "");
            PreparedStatement ps = conn.prepareStatement("create table users(id varchar(10) primary key," +
                    " name varchar(20) not null," +
                    " password varchar(10) not null)")) {
            ps.executeUpdate();
        }

        userDao = new UserDao();
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

    @After
    public void tearDown() throws SQLException {
        try(Connection conn = DriverManager.getConnection("jdbc:h2:~/object-dependency", "", "");
            PreparedStatement ps = conn.prepareStatement("drop table users")) {
            ps.executeUpdate();
        }
    }

}