package com.idibros.study.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Created by dongba on 2017-08-21.
 */
public class SimpleConnectionMaker {

    // 이슈 1. 상속을 통한 DB 변경사항에 대응하는 방법이 불가능 해졌다.(SimpleConnectionMaker는 h2 디비를 강제로 설정했음.)
    public Connection makeNewConnection () throws ClassNotFoundException, SQLException {
        Class.forName("org.h2.Driver");
        Connection conn = DriverManager.getConnection("jdbc:h2:~/object-dependency", "", "");
        return conn;
    }
}
