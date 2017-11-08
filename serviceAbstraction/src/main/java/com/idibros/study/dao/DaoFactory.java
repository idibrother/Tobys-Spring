package com.idibros.study.dao;

import com.idibros.study.aop.NameMatchClassMethodPointcut;
import com.idibros.study.factory.TransactionAdvice;
import com.idibros.study.factory.TxProxyFactoryBean;
import com.idibros.study.service.UserService;
import com.idibros.study.service.impl.TestUserServiceImpl;
import com.idibros.study.service.impl.UserServiceImpl;
import org.springframework.aop.framework.ProxyFactoryBean;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.aop.support.NameMatchMethodPointcut;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;

import javax.sql.DataSource;
import java.sql.SQLException;

/**
 * Created by dongba on 2017-08-22.
 * 스프링을 사용해서 DaoFactory가 어플리케이션 컨텍스트 역할을 하도록 변경
 */
@Configuration
public class DaoFactory {

    /**
     * 자동 프록시 팩토리를 활용하기 위해서 테스트 유저 서비스도 빈으로 등록해야 할 것 같다.
     * @return
     */
    @Bean
    public UserService testUserServiceImpl () {
        return new TestUserServiceImpl("foo21");
    }

    /**
     * 어드바이저를 이용하는 자동 프록시 생성기 빈을 등록한다.
     */
    @Bean
    public DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator () {
        return new DefaultAdvisorAutoProxyCreator();
    }

    /**
     * 포인트컷을 등록한다.
     * 포인트컷, 어드바이스, 어드바이저를 빈으로 등록하면 자동 프록시 생성기가 DI해서 사용하는 것 같다.
     * 포인트컷의 설정 정보에 대상 클래스와 메소드를 정의했기 때문에 이를 참조하는 듯 하다.
     * 그래서 별도로 타겟 지정 없이도 트랜젝션 기능이 잘 동작한다.
     * @return
     */
    @Bean
    public NameMatchClassMethodPointcut nameMatchClassMethodPointcut () {
        NameMatchClassMethodPointcut nameMatchClassMethodPointcut = new NameMatchClassMethodPointcut();
        nameMatchClassMethodPointcut.setMappedName("*ServiceImpl");
        nameMatchClassMethodPointcut.setMappedName("upgrade*");
        return nameMatchClassMethodPointcut;
    }

    @Bean
    public ProxyFactoryBean proxyFactoryBean() throws SQLException {
        ProxyFactoryBean proxyFactoryBean = new ProxyFactoryBean();
        proxyFactoryBean.addAdvisor(defaultPointcutAdvisor());
        return proxyFactoryBean;
    }

    @Bean
    public DefaultPointcutAdvisor defaultPointcutAdvisor () {
        DefaultPointcutAdvisor pointcutAdvisor = new DefaultPointcutAdvisor(nameMatchMethodPointcut(), transactionAdvice());
        return pointcutAdvisor;
    }

    @Bean
    public TransactionAdvice transactionAdvice() {
        TransactionAdvice transactionAdvice = new TransactionAdvice();
        transactionAdvice.setTransactionManager(dataSourceTransactionManager());
        return transactionAdvice;
    }

    @Bean
    public NameMatchMethodPointcut nameMatchMethodPointcut () {
        NameMatchMethodPointcut pointcut = new NameMatchMethodPointcut();
        pointcut.setMappedName("upgrade*");
        return pointcut;
    }

    @Bean
    public TxProxyFactoryBean userService() throws SQLException {
        TxProxyFactoryBean txProxyFactoryBean = new TxProxyFactoryBean();
        txProxyFactoryBean.setTarget(userServiceImpl());
        txProxyFactoryBean.setTransactionManager(dataSourceTransactionManager());
        txProxyFactoryBean.setPattern("upgradeLevels");
        txProxyFactoryBean.setServiceInterface(UserService.class);
        return txProxyFactoryBean;
    }

    @Bean
    public UserService userServiceImpl() throws SQLException {
        return new UserServiceImpl();
    }

    @Bean
    public UserDao userDao() {
        UserDao userDao = new UserDao();
        userDao.setJdbcTemplate(jdbcTemplate());
        return userDao;
    }

    @Bean
    public JdbcTemplate jdbcTemplate() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate();
        jdbcTemplate.setDataSource(dataSource());
        return jdbcTemplate;
    }

    @Bean
    public DataSourceTransactionManager dataSourceTransactionManager() {
      return new DataSourceTransactionManager(dataSource());
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
