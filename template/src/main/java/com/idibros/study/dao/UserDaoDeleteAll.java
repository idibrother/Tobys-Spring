package com.idibros.study.dao;

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
public class UserDaoDeleteAll extends UserDao {

    private Logger logger = LoggerFactory.getLogger(UserDaoDeleteAll.class);

    // DB Connection 외에 다른 기능을 가진 DataSource 인터페이스로 변경하였다.
    // 다양한 방법으로 DB Connection을 생성하는 구현체들이 있으므로 이를 활용하면 훨씬 범용적으로 사용 할 수 있을 것 같다.
    private DataSource dataSource;

    public UserDaoDeleteAll(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void deleteAll() throws SQLException {
        try(Connection conn = dataSource.getConnection();
            PreparedStatement ps = makeStatement(conn)) {
            ps.executeUpdate();
        } catch (SQLException e) {
            logger.error(e.getMessage());
            throw e;
        }
    }

    protected PreparedStatement makeStatement(Connection conn) throws SQLException {
        PreparedStatement ps = conn.prepareStatement("delete from users");
        return ps;
    }
}
