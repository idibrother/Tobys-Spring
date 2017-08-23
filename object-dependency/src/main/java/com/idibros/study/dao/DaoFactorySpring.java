package com.idibros.study.dao;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by dongba on 2017-08-22.
 */
@Configuration
public class DaoFactorySpring {

    @Bean
    public UserDao userDao() {
        UserDao userDao = new UserDao(connectionMaker());
        return userDao;
    }

    @Bean
    public AccountDao accountDao() {
        AccountDao accountDao = new AccountDao(connectionMaker());
        return accountDao;
    }

    @Bean
    public ConnectionMaker connectionMaker() {
        return new NConnectionMaker();
    }

}
