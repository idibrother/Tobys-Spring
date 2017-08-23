package com.idibros.study.dao;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

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
    /**
     * 동등성 테스트
     * DaoFactory는 스프링을 사용 할 때와 사용하지 않을 때 비슷한 형태로 보인다.
     * 다만, 스프링을 사용하지 않을 때는 두 개의 다른 객체 참조를 만들고 각각은 다른 객체이다.
     */
    public void equivalent_test() throws SQLException, ClassNotFoundException {
        DaoFactory daoFactory = new DaoFactory();
        AccountDao accountDao = daoFactory.accountDao();
        AccountDao accountDao1 = daoFactory.accountDao();
        assertThat(accountDao.equals(accountDao1), is(false));
    }

    @Test
    /**
     * 동일성 테스트
     * 스피링을 사용해서 DaoFactory를 구현 할 경우 동일한 객체에 여러 참조가 있는 형태이다.
     */
    public void identity_test() throws SQLException, ClassNotFoundException {
        ApplicationContext applicationContext = new AnnotationConfigApplicationContext(DaoFactorySpring.class);
        UserDao userDao = applicationContext.getBean("userDao", UserDao.class);
        UserDao userDao1 = applicationContext.getBean("userDao", UserDao.class);
        assertThat(userDao.equals(userDao1), is(true));
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