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
 * Created by dongba on 2017-08-21.
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
        try(Connection conn = dataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement("insert into users(id, name, password) values(?, ?, ?)")) {

            ps.setString(1, user.getId());
            ps.setString(2, user.getName());
            ps.setString(3, user.getPassword());

            ps.executeUpdate();
        }
    }

    public User get(String id) throws ClassNotFoundException, SQLException {
        User user = new User();
        try(Connection conn = dataSource.getConnection();
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

    public void deleteAll() throws SQLException {
//        /**
//         * 2. catch-finally 구문으로 처리해도 되지만 JDK7부터 추가된
//         * Closable 인터페이스의 구현체들은 아래와 같이 try문 뒤 괄호 안에 넣으면 같은 효과가 있다.
//         */
//        /**
//         * 아래와 같은 예외 처리 부분은 모든 메소드에서 반복해서 보이고,
//         * 메소드 수가 증가하거나 다른 클래스에서도 동일한 형태가 반복될 경우 코드 누락이 있을 경우
//         * 같은 이슈가 발생 할 가능성이 있다.
//         * 그래서 우선 메소드로 분리 해 볼 것이다.
//         */
        try(Connection conn = dataSource.getConnection();
            PreparedStatement ps = makeStatement(conn)) {
            ps.executeUpdate();
        } catch (SQLException e) {
            logger.error(e.getMessage());
            throw e;
        }

//        /**
//         * 1. 위 문장 실행 중에 예외가 발생하면 connection과 ps의 close가 실행이 안된 상태로 종료한다.
//         * 예외가 여러 번 반복 할 경우 가용한 connection 갯수가 줄어들게 되고,
//         * 어느 순간 가용 connection이 없을 수도 있다.
//         * 그래서 예외가 발생해도 문제가 없도록 만들어야 한다.
//         */
//        ps.close();
//        conn.close();
    }

    /**
     * 1. 우선 메소드로 분리를 시도한다.
     */
    private PreparedStatement makeStatement(Connection conn) throws SQLException {
        PreparedStatement ps = conn.prepareStatement("delete from users");
        return ps;
    }
}
