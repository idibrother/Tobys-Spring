package com.idibros.study.dao;

import com.idibros.study.dto.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by dongba on 2017-08-21.
 */
public class UserDao {

    // 인터페이스를 도입함으로써 ConnectionMaker에 대한 구현체를 UserDao가 몰라도 된다.
    private ConnectionMaker connectionMaker;

    public UserDao(ConnectionMaker connectionMaker) {
        // connectionMaker 구현체를 사용자가 결정 할 수 있도록 UserDao 생성자에서 매개변수를 추가하였다.
        this.connectionMaker = connectionMaker;
    }

    public void add(User user) throws ClassNotFoundException, SQLException {
        try(Connection conn = connectionMaker.makeConnection();
            PreparedStatement ps = conn.prepareStatement("insert into users(id, name, password) values(?, ?, ?)")) {

            ps.setString(1, user.getId());
            ps.setString(2, user.getName());
            ps.setString(3, user.getPassword());

            ps.executeUpdate();
        }
    }

    public User get(String id) throws ClassNotFoundException, SQLException {
        User user = new User();
        try(Connection conn = connectionMaker.makeConnection();
            PreparedStatement ps = conn.prepareStatement("select * from users where id = ?")) {

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
