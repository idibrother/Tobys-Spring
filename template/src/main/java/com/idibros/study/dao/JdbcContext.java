package com.idibros.study.dao;

import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Created by dongba on 2017-09-07.
 */
public class JdbcContext {

    private Logger logger = LoggerFactory.getLogger(JdbcContext.class);

    @Setter
    private DataSource dataSource;

    public void workWithStatementStrategy(StatementStrategy strategy) throws SQLException {
        try(Connection conn = dataSource.getConnection();
            PreparedStatement ps = strategy.makePreparedStatement(conn)) {
            ps.executeUpdate();
        } catch (SQLException e) {
            logger.error(e.getMessage());
            throw e;
        }
    }
}
