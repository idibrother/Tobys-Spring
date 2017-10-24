package com.idibros.study.service;

import com.idibros.study.dao.UserDao;
import com.idibros.study.dto.Level;
import com.idibros.study.dto.User;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
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

    @Autowired
    @Setter
    private DataSourceTransactionManager transactionManager;

    public void setUserDao (UserDao userDao) {
        this.userDao = userDao;
    }

    public void upgradeLevels() throws SQLException {
        TransactionStatus status = transactionManager.getTransaction(new DefaultTransactionDefinition());

        try {
            upgradeLevelsInternal();
            transactionManager.commit(status);
        } catch (Exception e) {
            transactionManager.rollback(status);
        }
    }

    private void upgradeLevelsInternal() {
        List<User> allUsers = userDao.getAll();
        for(User user : allUsers) {
            if (canUpgradeLevel(user)) {
                upgradeLevel(user);
            }
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
