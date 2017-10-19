package com.idibros.study.dao;

import com.idibros.study.dto.Level;
import com.idibros.study.dto.User;
import lombok.Data;
import lombok.Getter;
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

/**
 * 결과적으로 예외나 공통로직을 제외한 부분을 JdbcTemplate를 활용하도록 변경하여 UserDao가 깔끔해졌다.
 */
public class UserDao {

    private Logger logger = LoggerFactory.getLogger(UserDao.class);

    @Setter
    @Getter
    private JdbcTemplate jdbcTemplate;

    private RowMapper<User> userMapper = new RowMapper<User>() {
        @Override
        public User mapRow(ResultSet rs, int rowNum) throws SQLException {
            /**
             * 추가한 필드 목록에 대한 맵퍼 수정
             */
            User user = new User();
            user.setId(rs.getString("id"));
            user.setName(rs.getString("name"));
            user.setPassword(rs.getString("password"));
            user.setLevel(Level.valueOf(rs.getInt("level")));
            user.setLoginCount(rs.getInt("logincount"));
            user.setRecommendCount(rs.getInt("recommendcount"));
            return user;
        }
    };

    public void updateTable() {
//        this.jdbcTemplate.update("drop table users");
        this.jdbcTemplate.update("create table users (" +
                "id varchar(10) primary key, " +
                "name varchar(20) not null, " +
                "password varchar(10) not null, " +
                "level int not null, " +
                "logincount int not null, " +
                "recommendcount int not null)");
    }

    public void add(User user) throws ClassNotFoundException, SQLException {
        this.jdbcTemplate.update("insert into users(id, name, password, level, logincount, recommendcount) values(?, ?, ?, ?, ?, ?)",
                user.getId(), user.getName(), user.getPassword(), user.getLevel().intValue(), user.getLoginCount(), user.getRecommendCount());
    }

    public void deleteAll() throws SQLException {
        jdbcTemplate.update("delete from users");
    }

    public User get(String id) throws ClassNotFoundException, SQLException {
        User user = jdbcTemplate.queryForObject("select * from users where id = ?",
                new Object[]{id}, userMapper);

        return user;
    }

    public int getCount() {
        Integer result = this.jdbcTemplate.queryForObject("select count(*) from users", Integer.class);
        return result;
    }

    public List<User> getAll() {
        List<User> result = this.jdbcTemplate.query("select * from users order by id", userMapper);
        return result;
    }

    public void update(User user) {
        /**
         * userId에 대하여 수정 기능을 추가하였다.
         */
        this.jdbcTemplate.update("update users set name = ?, password = ?, level = ?, logincount = ?, " +
                "recommendcount = ? " +
                "where id = ?",
                user.getName(), user.getPassword(), user.getLevel().intValue(), user.getLoginCount(), user.getRecommendCount(), user.getId());
    }
}

