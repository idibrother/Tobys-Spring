package com.idibros.study.dao;

import com.idibros.study.service.TestUserService;
import com.idibros.study.service.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Created by dongba on 2017-10-18.
 */

@Configuration
public class DaoTestFactory extends DaoFactory {

    @Primary
    @Bean
    public UserService userService() throws SQLException {
        TestUserService userService = new TestUserService("foo21");
        UserDao userDao = userDao();
        userService.setUserDao(userDao);
        return userService;
    }

}
