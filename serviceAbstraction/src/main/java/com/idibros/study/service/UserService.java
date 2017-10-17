package com.idibros.study.service;

import com.idibros.study.dao.UserDao;
import com.idibros.study.dto.Level;
import com.idibros.study.dto.User;

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

    public void upgradeLevels() {
        /**
         * 아래와 같이 현재 레벨을 체크하고 변경 후 Dao를 통한 데이터 수정이 가능하지만
         * 다음 레벨이 무엇인지 내포하고 있다는 것과 업그레이드 하는 작업이 동시에 적용되어 있는 코드이다.
         * 레벨이 추가될 경우 if문과 enum이 같이 수정되어야 한다.
         */
        List<User> allUsers = userDao.getAll();

        for(User user : allUsers) {
            /**
             * 그래서 업그레이드 가능 여부 체크 후에
             * 다음 레벨이 무엇인지에 대한 것은 enum에 요청하고,
             * 레벨 업그레이드 작업을 User에 요청하도록 변경한다.
             */
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

    private void upgradeLevel(User user) {
        user.upgradeLevel();
        userDao.update(user);
    }
}
