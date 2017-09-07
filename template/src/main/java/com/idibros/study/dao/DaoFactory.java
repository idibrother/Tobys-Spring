package com.idibros.study.dao;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;

import javax.sql.DataSource;

/**
 * Created by dongba on 2017-08-22.
 * 스프링을 사용해서 DaoFactory가 어플리케이션 컨텍스트 역할을 하도록 변경
 */
@Configuration
public class DaoFactory {

    @Bean
    public UserDaoDeleteAll userDaoDeleteAll() {
        UserDaoDeleteAll userDaoDeleteAll = new UserDaoDeleteAll(dataSource());
        return userDaoDeleteAll;
    }

    @Bean
    public UserDaoAdd userDaoAdd() {
        UserDaoAdd userDaoAdd = new UserDaoAdd(dataSource());
        return userDaoAdd;
    }

    @Bean
    public UserDaoGet userDaoGet() {
        UserDaoGet userDaoGet = new UserDaoGet(dataSource());
        return userDaoGet;
    }

    @Bean
    // 동일한 connectionMaker를 사용하는 DAO의 종류가 여러 개일 경우를 고려해서 connectionMaker를 생성하는 별도의 함수를 정의하였다.
    public DataSource dataSource() {
        SimpleDriverDataSource dataSource = new SimpleDriverDataSource();

        dataSource.setDriverClass(org.h2.Driver.class);
        dataSource.setUrl("jdbc:h2:~/object-dependency");
        dataSource.setUsername("");
        dataSource.setPassword("");

        return dataSource;
    }

}
