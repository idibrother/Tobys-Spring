package com.idibros.study.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Created by dongba on 2017-09-07.
 */

/**
 * 1. 변경되는 부분은 전략으로 분류하였다.
 */
public interface StatementStrategy {

    PreparedStatement makePreparedStatement(Connection conn) throws SQLException;

}
