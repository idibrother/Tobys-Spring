package com.idibros.study.dao;

import com.idibros.study.dto.User;
import org.springframework.dao.EmptyResultDataAccessException;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by dongba on 2017-08-21.
 */
public class UserDao {

    // DB Connection 외에 다른 기능을 가진 DataSource 인터페이스로 변경하였다.
    // 다양한 방법으로 DB Connection을 생성하는 구현체들이 있으므로 이를 활용하면 훨씬 범용적으로 사용 할 수 있을 것 같다.
    private DataSource dataSource;

    public UserDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void add(User user) throws SQLException {
        try(Connection conn = dataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement("insert into users(id, name, password) values(?, ?, ?)")) {

            ps.setString(1, user.getId());
            ps.setString(2, user.getName());
            ps.setString(3, user.getPassword());

            ps.executeUpdate();
        }
    }

    public User get(String id) throws SQLException {
        User user = null;
        try(Connection conn = dataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement("select * from users where id = ?")) {

            ps.setString(1, id);

            try(ResultSet rs = ps.executeQuery()) {

                // 쿼리 실행 결과 데이터가 있을 경우만 user를 생성 후에 리턴 해준다.
                if (rs.next()) {
                    user = new User();
                    user.setId(rs.getString("id"));
                    user.setName(rs.getString("name"));
                    user.setPassword(rs.getString("password"));
                }
            }
        }

        // user가 없을 경우 예외를 발생시키도록 보완하였다.
        if (user == null) {
            throw new EmptyResultDataAccessException(1);
        }

        return user;
    }

    public void deleteAll() throws SQLException {
        try(Connection conn = dataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement("delete from users")) {

            ps.executeUpdate();
        }
    }

    public int getCount() throws SQLException {
        try(Connection conn = dataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement("select count(*) from users")) {

            try(ResultSet rs = ps.executeQuery()) {
                rs.next();

                int count = rs.getInt(1);

                return count;
            }
        }
    }
}
