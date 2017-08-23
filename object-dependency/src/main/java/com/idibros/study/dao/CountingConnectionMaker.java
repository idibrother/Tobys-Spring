package com.idibros.study.dao;

import lombok.Getter;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Created by dongba on 2017-08-23.
 */
@Getter
public class CountingConnectionMaker implements ConnectionMaker {

    private ConnectionMaker connectionMaker;

    private int counter = 0;

    public CountingConnectionMaker(ConnectionMaker connectionMaker) {
        this.connectionMaker = connectionMaker;
    }

    @Override
    public Connection makeConnection() throws ClassNotFoundException, SQLException {
        this.counter++;
        return this.connectionMaker.makeConnection();
    }
}
