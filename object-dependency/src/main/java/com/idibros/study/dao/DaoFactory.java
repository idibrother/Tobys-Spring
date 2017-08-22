package com.idibros.study.dao;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by dongba on 2017-08-22.
 * 스프링을 사용해서 DaoFactory가 어플리케이션 컨텍스트 역할을 하도록 변경
 */
@Configuration
public class DaoFactory {

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
    // 동일한 connectionMaker를 사용하는 DAO의 종류가 여러 개일 경우를 고려해서 connectionMaker를 생성하는 별도의 함수를 정의하였다.
    public ConnectionMaker connectionMaker() {
        return new NConnectionMaker();
    }

}
