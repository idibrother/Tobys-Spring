package com.idibros.study.dao;

import com.idibros.study.dto.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Created by dongba on 2017-08-21.
 */

/**
 * 1. 템플릿 메소드 패턴을 활용하여 중복을 제거해본다.
 */
public class UserDaoAdd extends UserDao {

    private Logger logger = LoggerFactory.getLogger(UserDaoAdd.class);

    // DB Connection 외에 다른 기능을 가진 DataSource 인터페이스로 변경하였다.
    // 다양한 방법으로 DB Connection을 생성하는 구현체들이 있으므로 이를 활용하면 훨씬 범용적으로 사용 할 수 있을 것 같다.
    private DataSource dataSource;

    public UserDaoAdd(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void add(User user) throws ClassNotFoundException, SQLException {
        try(Connection conn = dataSource.getConnection();
            PreparedStatement ps = makeStatement(conn)) {

            ps.setString(1, user.getId());
            ps.setString(2, user.getName());
            ps.setString(3, user.getPassword());

            ps.executeUpdate();
        }
    }

    protected PreparedStatement makeStatement(Connection conn) throws SQLException {
        PreparedStatement ps = conn.prepareStatement("insert into users(id, name, password) values(?, ?, ?)");
        return ps;
    }
}
