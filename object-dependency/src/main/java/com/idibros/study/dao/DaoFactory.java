package com.idibros.study.dao;

/**
 * Created by dongba on 2017-08-22.
 */
public class DaoFactory {

    public UserDao userDao() {
        UserDao userDao = new UserDao(connectionMaker());
        return userDao;
    }

    public AccountDao accountDao() {
        AccountDao accountDao = new AccountDao(connectionMaker());
        return accountDao;
    }

    // 동일한 connectionMaker를 사용하는 DAO의 종류가 여러 개일 경우를 고려해서 connectionMaker를 생성하는 별도의 함수를 정의하였다.
    public ConnectionMaker connectionMaker() {
        return new NConnectionMaker();
    }

}
