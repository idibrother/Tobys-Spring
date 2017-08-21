package com.idibros.study.dao;

import com.idibros.study.dto.User;

import java.sql.*;

/**
 * Created by dongba on 2017-08-21.
 */
public class UserDao {

    // 인터페이스를 도입함으로써 ConnectionMaker에 대한 구현체를 UserDao가 몰라도 된다.
    private ConnectionMaker simpleConnectionMaker;

    public UserDao() {
        // 하지만 여전히 구현체의 내용을 UserDao가 알고 있다는 구조상의 이슈가 있다.
        simpleConnectionMaker = new NConnectionMaker();
    }

    public void add(User user) throws ClassNotFoundException, SQLException {
        try(Connection conn = simpleConnectionMaker.makeConnection();
            PreparedStatement ps = conn.prepareStatement("insert into users(id, name, password) values(?, ?, ?)")) {

            ps.setString(1, user.getId());
            ps.setString(2, user.getName());
            ps.setString(3, user.getPassword());

            ps.executeUpdate();
        }
    }

    public User get(String id) throws ClassNotFoundException, SQLException {
        Class.forName("org.h2.Driver");
        User user = new User();
        try(Connection conn = DriverManager.getConnection("jdbc:h2:~/object-dependency", "", "");
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
