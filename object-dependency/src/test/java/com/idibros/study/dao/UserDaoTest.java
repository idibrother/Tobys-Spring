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

    private static UserDao userDao;

    @BeforeClass
    public static void init() throws ClassNotFoundException {
        // 사용자가 구현체를 알고 있어도 되지만,
        // 이런 책임을 직접 담당하도록 하는 것 보다는 ConnectionMaker와 DAO의 관계를 맺어주는 책임을 다른 클래스한테 넘겼다.(DaoFactory)
        // 그래서 사용자가 UserDao가 어떻게 초기화되는지 신경쓰지 않은 구조가 되었다.
        // 사용자가 사용 할 객체를 능동적으로 생성해서 사용하는 형태인 라이브러리가 아니고
        // DaoFactory(IoC 개념을 적용한 객체 생성 및 객체간의 관계를 설정을 담당)가 만들어주는 객체를 사용하는 수동적인 구조로 바꼈다.
        // 이것이 제어의 역전(inversion of controll)이다.
        userDao = new DaoFactory().userDao();
    }

    // 유닛 테스트마다 상호 독립적으로 수행 할 수 있도록 각 테스트를 실행 할 때 마다 테이블을 제거하고 새로 만듦.
    @Before
    public void setUp() throws ClassNotFoundException, SQLException {
        // table 생성
        try(Connection conn = DriverManager.getConnection("jdbc:h2:~/object-dependency", "", "");
            PreparedStatement ps = conn.prepareStatement("create table users(id varchar(10) primary key," +
                    " name varchar(20) not null," +
                    " password varchar(10) not null)")) {
            ps.executeUpdate();
        }

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
        // 테이블 제거
        try(Connection conn = DriverManager.getConnection("jdbc:h2:~/object-dependency", "", "");
            PreparedStatement ps = conn.prepareStatement("drop table users")) {
            ps.executeUpdate();
        }
    }

}