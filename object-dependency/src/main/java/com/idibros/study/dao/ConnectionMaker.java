package com.idibros.study.dao;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Created by dongba on 2017-08-21.
 */
public interface ConnectionMaker {

    public Connection makeConnection() throws ClassNotFoundException, SQLException;

}
