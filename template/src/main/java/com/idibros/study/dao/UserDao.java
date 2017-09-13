package com.idibros.study.dao;

import com.idibros.study.dto.User;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by dongba on 2017-09-07.
 */
public class UserDao {

    private Logger logger = LoggerFactory.getLogger(UserDao.class);

    @Setter
    private JdbcTemplate jdbcTemplate;

    public void add(User user) throws ClassNotFoundException, SQLException {
        this.jdbcTemplate.update("insert into users(id, name, password) values(?, ?, ?)",
                user.getId(), user.getName(), user.getPassword());
    }

    public void deleteAll() throws SQLException {
        jdbcTemplate.update("delete from users");
    }

    public User get(String id) throws ClassNotFoundException, SQLException {
        /**
         * 1. JdbcTemplate를 활용하여 user 객체를 가져오도록 변경하였다.
         * 2. queryForObject 함수에 SQL문과 조건문에서 사용할 변수, SQL 실행 결과를 역직렬화 할 mapper callback을 전달해서
         *    user 객체를 조회한다.
         */
        User user = jdbcTemplate.queryForObject("select * from users where id = ?",
                new Object[]{id},
                new RowMapper<User>() {
                    @Override
                    public User mapRow(ResultSet rs, int rowNum) throws SQLException {
                        /**
                         * rs는 제일 첫 번 째 row를 가리키고 있으므로 rs.next를 별도로 호출 할 필요는 없다.
                         */
                        User user = new User();
                        user.setId(rs.getString("id"));
                        user.setName(rs.getString("name"));
                        user.setPassword(rs.getString("password"));
                        return user;
                    }
                });

        return user;
    }

    public int getCount() {
        Integer result = this.jdbcTemplate.queryForObject("select count(*) from users", Integer.class);
        return result;
    }

    public List<User> getAll() {
        List<User> result = this.jdbcTemplate.query("select * from users order by id",
                new RowMapper<User>() {
                    @Override
                    public User mapRow(ResultSet rs, int rowNum) throws SQLException {
                        User user = new User();
                        user.setId(rs.getString("id"));
                        user.setName(rs.getString("name"));
                        user.setPassword(rs.getString("password"));
                        return user;
                    }
                });
        return result;
    }
}

