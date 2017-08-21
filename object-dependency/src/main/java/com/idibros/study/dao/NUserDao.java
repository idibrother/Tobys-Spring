package com.idibros.study.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Created by dongba on 2017-08-21.
 */
public class NUserDao extends UserDao {

    @Override
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:h2:~/object-dependency", "", "");
    }

}
