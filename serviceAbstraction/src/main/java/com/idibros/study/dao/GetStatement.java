package com.idibros.study.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Created by dongba on 2017-09-07.
 */
public class GetStatement implements StatementStrategy {

    @Override
    public PreparedStatement makePreparedStatement(Connection conn) throws SQLException {
        PreparedStatement ps = conn.prepareStatement("select * from users where id = ?");
        return ps;
    }

}
