package com.idibros.study.service;

import com.idibros.study.dao.UserDao;
import com.idibros.study.dto.Level;
import com.idibros.study.dto.User;
import lombok.Setter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by dongba on 2017-10-16.
 */
public class UserService {

    public static final int MIN_LOGCOUNT_FOR_SILVER = 50;
    public static final int MIN_RECOMMEND_FOR_GOLD = 30;
    private UserDao userDao;

    @Setter
    private DataSource dataSource;

    public void setUserDao (UserDao userDao) {
        this.userDao = userDao;
    }

    public void upgradeLevels() throws SQLException {
        /**
         * 트랜젝션 동기화를 사용해서 롤백이 성공하였다.
         * JdbcTemplate은 동기화 저장소에 등록된 DB커넥션이나 트랜젝션이 없으면 직접 생성해서 사용한다고 한다.
         * 반면 아래와 같이 동기화 저장소 설정을 하면 저장소에 있는 커넥션을 가져와서 사용한다고 한다.
         */
        TransactionSynchronizationManager.initSynchronization();
        Connection c = DataSourceUtils.getConnection(dataSource);
        c.setAutoCommit(false);

        /**
         * 트랜젝션 관리를 위해 아래와 같이 connection에 대해서 autoCommit을 false로 세팅한다.
         * for문 실행 중 에러가 없으면 commit을 하고,
         * 예외가 발생하면 rollback한다.
         * 하지만 아래와 같은 코드는 비즈니스 로직과 DAO 로직이 혼합되어 나오므로 분리했던 노력들이 의미가 없어진다.
         * 모든 유저의 레벨을 업그레이드 하는 트랜젝션 생성을 위한 구조로 변경을 검토해본다.
         */
        try {
            List<User> allUsers = userDao.getAll();
            for(User user : allUsers) {
                if (canUpgradeLevel(user)) {
                    upgradeLevel(user);
                }
            }
            c.commit();
        } catch (Exception e) {
            c.rollback();
        } finally {
            /**
             * 트랜젝션이 종료되면 스프링 데이터소스 유틸을 사용해서 안전하게 닫아줘야 되나보다.
             * JdbcTemplate이 이런 동작을 자동으로 해 주지만 해당 케이스는 동기화 저장소에 있는 커넥션을 사용했기 때문에
             * 그런것 같다.
             */
            DataSourceUtils.releaseConnection(c, dataSource);
            TransactionSynchronizationManager.unbindResource(this.dataSource);
            TransactionSynchronizationManager.clearSynchronization();
        }
    }

    private boolean canUpgradeLevel(User user) {
        Level currLevel = user.getLevel();
        switch(currLevel) {
            case BASIC: return (user.getLoginCount() >= MIN_LOGCOUNT_FOR_SILVER);
            case SILVER: return (user.getRecommendCount() >= MIN_RECOMMEND_FOR_GOLD);
            case GOLD: return false;
            default: throw new IllegalArgumentException("Unknown Level: " +
                        currLevel);
        }
    }

    public void add(User user) throws SQLException, ClassNotFoundException {
        if (user.getLevel() == null) {
            user.setLevel(Level.BASIC);
        }
        userDao.add(user);
    }

    protected void upgradeLevel(User user) {
        user.upgradeLevel();
        userDao.update(user);
    }
}
