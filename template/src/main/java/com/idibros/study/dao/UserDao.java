package com.idibros.study.dao;

import com.idibros.study.dto.User;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.ResultSetExtractor;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by dongba on 2017-09-07.
 */
public class UserDao {

    private Logger logger = LoggerFactory.getLogger(UserDao.class);

    @Setter
    private JdbcTemplate jdbcTemplate;

    // DB Connection 외에 다른 기능을 가진 DataSource 인터페이스로 변경하였다.
    // 다양한 방법으로 DB Connection을 생성하는 구현체들이 있으므로 이를 활용하면 훨씬 범용적으로 사용 할 수 있을 것 같다.
    private DataSource dataSource;

    public UserDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void add(User user) throws ClassNotFoundException, SQLException {
        this.jdbcTemplate.update("insert into users(id, name, password) values(?, ?, ?)",
                user.getId(), user.getName(), user.getPassword());
    }

    public void deleteAll() throws SQLException {
        jdbcTemplate.update("delete from users");
    }

    public User get(String id) throws ClassNotFoundException, SQLException {
        User user = new User();
        StatementStrategy statement = new GetStatement();
        try(Connection conn = dataSource.getConnection();
            PreparedStatement ps = statement.makePreparedStatement(conn)) {

            ps.setString(1, id);

            try(ResultSet rs = ps.executeQuery()) {
                rs.next();

                user.setId(rs.getString("id"));
                user.setName(rs.getString("name"));
                user.setPassword(rs.getString("password"));
            }
        }

        return user;
    }

    public int getCount() {
        /**
         * 1. callback 2개를 template에 전달해서 실행 한 결과를 getCount가 활용한다.
         */
        Integer result = this.jdbcTemplate.query(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                return con.prepareStatement("select count(*) from users");
            }
        }, new ResultSetExtractor<Integer>() {
            @Override
            public Integer extractData(ResultSet rs) throws SQLException, DataAccessException {
                rs.next();
                return rs.getInt(1);
            }
        });
        return result;
    }
}

