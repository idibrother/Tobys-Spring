package com.idibros.study.dao;

import com.idibros.study.dto.Level;
import com.idibros.study.dto.User;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
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

    private User user1;

    private User user2;

    private User user3;

    @BeforeClass
    public static void init() throws ClassNotFoundException {
        ApplicationContext context = new AnnotationConfigApplicationContext(DaoFactory.class);
        userDao = context.getBean("userDao", UserDao.class);
    }

    // 유닛 테스트마다 상호 독립적으로 수행 할 수 있도록 각 테스트를 실행 할 때 마다 테이블을 제거하고 새로 만듦.
    @Before
    public void setUp() throws ClassNotFoundException, SQLException {
        userDao.deleteAll();
        /**
         * BASIC Level의 회원이 50번 이상 로그인 하면 SILVER가 되고,
         * SILVER Level의 회원이 30번 이상 추천을 받으면 GOLD가 되는 서비스 기능을 추가한다고 가정한다.
         */
        this.user1 = new User("foo1", "bar1", "pw1", Level.BASIC, 1, 0);
        this.user2 = new User("foo2", "bar2", "pw2", Level.SILVER, 55, 10);
        this.user3 = new User("foo3", "bar3", "pw3", Level.GOLD, 100, 40);
    }

    @Test
    @Ignore
    public void inii() {
        System.out.println();
        userDao.updateTable();
    }

    @Test
    public void get() throws SQLException, ClassNotFoundException {
        userDao.add(user1);

        User result = userDao.get(user1.getId());

        checksumUser(user1, result);
    }

    @Test(expected = EmptyResultDataAccessException.class)
    public void get_id에_해당하는_조회결과가_없을경우() throws SQLException, ClassNotFoundException {
        userDao.get("foo");
    }

    @Test
    public void getCount() throws SQLException, ClassNotFoundException {
        userDao.add(user1);
        int count = userDao.getCount();
        assertThat(count, is(1));

        userDao.add(user2);
        count = userDao.getCount();
        assertThat(count, is(2));
    }

    @Test
    public void getAll() throws SQLException, ClassNotFoundException {
        userDao.add(this.user1);
        int count = userDao.getCount();
        assertThat(count, is(1));

        List<User> userList = userDao.getAll();
        User resultUser1 = userList.get(0);
        checksumUser(user1, resultUser1);

        userDao.add(this.user2);
        count = userDao.getCount();
        assertThat(count, is(2));

        userList.clear();
        userList.addAll(userDao.getAll());
        resultUser1 = userList.get(0);
        User resultUser2 = userList.get(1);
        checksumUser(user1, resultUser1);
        checksumUser(user2, resultUser2);

        userDao.add(this.user3);
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
        List<User> result = userDao.getAll();
        assertThat(result.size(), is(0));
    }

    private void checksumUser(User user1, User resultUser1) {
        assertThat(user1.getId(), is(resultUser1.getId()));
        assertThat(user1.getName(), is(resultUser1.getName()));
        assertThat(user1.getPassword(), is(resultUser1.getPassword()));
        /**
         * 데이터 검증 할 때 추가된 필드에 대한 내용도 포함시킨다.
         */
        assertThat(user1.getLevel(), is(resultUser1.getLevel()));
        assertThat(user1.getLoginCount(), is(resultUser1.getLoginCount()));
        assertThat(user1.getRecommendCount(), is(resultUser1.getRecommendCount()));
    }

}