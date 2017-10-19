package com.idibros.study.service;

import com.idibros.study.dao.UserDao;
import com.idibros.study.dto.Level;
import com.idibros.study.dto.User;
import org.springframework.jdbc.core.JdbcTemplate;

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

    public void setUserDao (UserDao userDao) {
        this.userDao = userDao;
    }

    public void upgradeLevels() throws SQLException {
        List<User> allUsers = userDao.getAll();

        /**
         * 트랜젝션 관리를 위해 아래와 같이 connection에 대해서 autoCommit을 false로 세팅한다.
         * for문 실행 중 에러가 없으면 commit을 하고,
         * 예외가 발생하면 rollback한다.
         * 하지만 아래와 같은 코드는 비즈니스 로직과 DAO 로직이 혼합되어 나오므로 분리했던 노력들이 의미가 없어진다.
         * 모든 유저의 레벨을 업그레이드 하는 트랜젝션 생성을 위한 구조로 변경을 검토해본다.
         */
        JdbcTemplate jdbcTemplate = userDao.getJdbcTemplate();
        DataSource dataSource = jdbcTemplate.getDataSource();
        Connection c = dataSource.getConnection();
        /**
         * 원인은 모르겠지만 오토커밋 off가 안되거나 롤백이 제대로 안되는 것 같다.
         */
        c.setAutoCommit(false);
        try {
            for(User user : allUsers) {
                if (canUpgradeLevel(user)) {
                    upgradeLevel(user);
                }
            }
        } catch (Exception e) {
            c.rollback();
            try {
                User foo11 = userDao.get("foo11");
                System.out.println();
            } catch (ClassNotFoundException e1) {
                e1.printStackTrace();
            }
            System.out.println();
        } finally {
            c.commit();
            c.close();
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
