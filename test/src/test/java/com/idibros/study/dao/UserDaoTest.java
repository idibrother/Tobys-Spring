package com.idibros.study.dao;

import com.idibros.study.dto.User;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.dao.EmptyResultDataAccessException;

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
        // DaoFactory를 스프링 어플리케이션 컨택스트로 변경 후 객체의 생성과 관계의 설정하는 역할을 맡겼다.
        ApplicationContext context = new AnnotationConfigApplicationContext(DaoFactory.class);
        userDao = context.getBean("userDao", UserDao.class);
    }

    // 유닛 테스트마다 상호 독립적으로 수행 할 수 있도록 각 테스트를 실행 할 때 마다 테이블을 제거하고 새로 만듦.
    @Before
    public void setUp() throws ClassNotFoundException, SQLException {
        userDao.deleteAll();
    }

    @Test
    public void addAndGet() throws SQLException, ClassNotFoundException {
        User user = new User();
        user.setId("foo");
        user.setName("idibros");
        user.setPassword("password");

        User user1 = new User();
        user1.setId("bar");
        user1.setName("blabla");
        user1.setPassword("password");

        userDao.add(user);
        userDao.add(user1);

        User result = userDao.get(user.getId());
        assertThat(result.getId(), is(user.getId()));
        assertThat(result.getName(), is(user.getName()));
        assertThat(result.getPassword(), is(user.getPassword()));

        // 정말 id에 대응하는 User가 리턴되는 지를 확인하기 위해 다른 User를 추가하고 검증하도록 테스트를 좀 더 보완했다.
        User result2 = userDao.get(user1.getId());
        assertThat(result2.getId(), is(user1.getId()));
        assertThat(result2.getName(), is(user1.getName()));
        assertThat(result2.getPassword(), is(user1.getPassword()));
    }

    @Test(expected = EmptyResultDataAccessException.class)
    public void getUserFailure() throws SQLException {
        // 없는 데이터를 조회 할 경우 예외 발생 여부를 확인하도록 추가 테스트를 작성하였다.
        assertThat(userDao.getCount(), is(0));

        // 테스트를 만들 때는 최대한 문제의 여지가 있다고 가정하고 작성하는 습관을 가지는 것이 좋다고 한다.
        userDao.get("unknown-user");
    }

}