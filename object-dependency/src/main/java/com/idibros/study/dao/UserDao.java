package com.idibros.study.dao;

import com.idibros.study.dto.User;

import java.sql.*;

/**
 * Created by dongba on 2017-08-21.
 */
public class UserDao {

    private SimpleConnectionMaker simpleConnectionMaker;

    public UserDao() {
        // 이슈 2. 다른 ConnectionMaker를 구현하면 UserDao를 수정해야 한다.
        simpleConnectionMaker = new SimpleConnectionMaker();
    }

    public void add(User user) throws ClassNotFoundException, SQLException {
        try(Connection conn = simpleConnectionMaker.makeNewConnection();
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
