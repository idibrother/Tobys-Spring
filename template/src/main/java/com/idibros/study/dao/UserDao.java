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

/**
 * 2. 그리고 각 메소드마다 전략을 적용하여 중복을 제거하였다.
 * 하지만 각 메소드마다 전략의 구현체를 알고 있는 구조이기 때문에 전략 패턴이나 OCP 패턴에 적절하지 않다.
 * 그래서 구현체를 생성하고, 구조를 설정하는 부분을 jdbcContext에 위임하는 구조로 변경해볼 예정이다.
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
        StatementStrategy statement = new AddStatement();
        try(Connection conn = dataSource.getConnection();
            PreparedStatement ps = statement.makePreparedStatement(conn)) {

            ps.setString(1, user.getId());
            ps.setString(2, user.getName());
            ps.setString(3, user.getPassword());

            ps.executeUpdate();
        }
    }

    public void deleteAll() throws SQLException {
        StatementStrategy statement = new DeleteAllStatement();
        try(Connection conn = dataSource.getConnection();
            PreparedStatement ps = statement.makePreparedStatement(conn)) {
            ps.executeUpdate();
        } catch (SQLException e) {
            logger.error(e.getMessage());
            throw e;
        }
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

}

