package com.idibros.study.dao;

/**
 * Created by dongba on 2017-08-22.
 */
public class DaoFactory {

    public UserDao userDao() {
        ConnectionMaker connectionMaker = new NConnectionMaker();
        UserDao userDao = new UserDao(connectionMaker);
        return userDao;
    }

}
