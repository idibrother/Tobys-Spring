package com.idibros.study;

import com.idibros.study.dao.AccountDao;
import com.idibros.study.dao.UserDao;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;

import javax.sql.DataSource;

/**
 * Created by dongba on 2017-08-24.
 * 테스트 전용 컨텍스트
 */
@Configuration
public class TestDaoContext {

    @Bean
    public UserDao userDao() {
        UserDao userDao = new UserDao(dataSource());
        return userDao;
    }

    @Bean
    public AccountDao accountDao() {
        AccountDao accountDao = new AccountDao(dataSource());
        return accountDao;
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
