package com.idibros.study.dao;

import com.idibros.study.dto.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    // DB Connection 외에 다른 기능을 가진 DataSource 인터페이스로 변경하였다.
    // 다양한 방법으로 DB Connection을 생성하는 구현체들이 있으므로 이를 활용하면 훨씬 범용적으로 사용 할 수 있을 것 같다.
    private DataSource dataSource;

    public UserDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void add(User user) throws ClassNotFoundException, SQLException {
        /**
         * 1. 전략에 추가정보를 전달 할 때는 setter나 생성자를 활용한다.
         */
        StatementStrategy strategy = new AddStatement(user);
        jdbcContextWithStatementStrategy(strategy);
    }

    public void deleteAll() throws SQLException {
        StatementStrategy strategy = new DeleteAllStatement();
        jdbcContextWithStatementStrategy(strategy);
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

    public void jdbcContextWithStatementStrategy(StatementStrategy strategy) throws SQLException {
        try(Connection conn = dataSource.getConnection();
            PreparedStatement ps = strategy.makePreparedStatement(conn)) {
            ps.executeUpdate();
        } catch (SQLException e) {
            logger.error(e.getMessage());
            throw e;
        }
    }

}

