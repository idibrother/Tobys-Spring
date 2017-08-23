package com.idibros.study.dao;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by dongba on 2017-08-22.
 * 스프링을 사용해서 DaoFactory가 어플리케이션 컨텍스트 역할을 하도록 변경
 */
@Configuration
public class CountingDaoFactory {

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
    // DB의 접속 횟수를 파악하기 위한 기능을 추가한 ConnectionMaker를 구현하여 검토 후에 제거하기 쉬운 구조로도 이용 가능하다.
    // 이전에 만들었던 DaoFactory에서 잠시 connectionMaker 함수를 수정 할 수도 있고,
    // 새로운 CountingDaoFactory를 만들어서 잠깐 사용 후에 다시 DaoFactory를 사용하면 된다.
    // 실제 연결은 기존에 사용하던 connectionMaker가 담당하고, 그 것에 의존하는 CountingConnectionMaker를 추가로 만들었다.
    public ConnectionMaker connectionMaker() {
        return new CountingConnectionMaker(realConnectionMaker());
    }

    @Bean
    public ConnectionMaker realConnectionMaker() {
        return new NConnectionMaker();
    }

}
